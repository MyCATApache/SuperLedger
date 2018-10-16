/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package drcc

import (
	"encoding/json"
	"fmt"
	"phdc/hdcc"
	"phdc/uicc"
	"testing"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

func checkInit(t *testing.T, stub *shim.MockStub, args [][]byte) {
	res := stub.MockInit("1", args)
	if res.Status != shim.OK {
		fmt.Println("Init failed", string(res.Message))
		t.FailNow()
	}
}

func checkState(t *testing.T, stub *shim.MockStub, name string, value string) {
	bytes := stub.State[name]
	if bytes == nil {
		fmt.Println("State", name, "failed to get value")
		t.FailNow()
	}
	if string(bytes) != value {
		fmt.Println("State value", name, "was not", value, "as expected")
		t.FailNow()
	}
}

func checkQuery(t *testing.T, stub *shim.MockStub, name string, value string) {
	res := stub.MockInvoke("1", [][]byte{[]byte("query"), []byte(name)})
	if res.Status != shim.OK {
		fmt.Println("Query", name, "failed", string(res.Message))
		t.FailNow()
	}
	if res.Payload == nil {
		fmt.Println("Query", name, "failed to get value")
		t.FailNow()
	}
	if string(res.Payload) != value {
		fmt.Println("Query value", name, "was not", value, "as expected")
		t.FailNow()
	}
}

func checkInvoke(t *testing.T, stub *shim.MockStub, args [][]byte) {
	res := stub.MockInvoke("1", args)
	if res.Status != shim.OK {
		fmt.Println("Invoke", args, "failed", string(res.Message))
		t.FailNow()
	}
}

func checkRet(t *testing.T, stub *shim.MockStub, args [][]byte) []byte {
	res := stub.MockInvoke("1", args)
	if res.Status != shim.OK {
		fmt.Println("Invoke", args, "failed", string(res.Message))
		t.FailNow()
	}
	return res.GetPayload()
}

func addInvite(t *testing.T, stub *shim.MockStub, ivt InviteArgs) []byte {
	// Init A=567 B=678

	b, err := json.Marshal(ivt)
	if err != nil {
		fmt.Println("json.Marshal ", err.Error())
		t.FailNow()
	}
	b = checkRet(t, stub, [][]byte{[]byte("invite"), b})
	fmt.Println("invite ret : ", string(b))
	return b
}

func addHealthData(t *testing.T, stub *shim.MockStub, itemID, result string) {
	testVal := hdcc.HealthData{
		UserID:   "4567",
		DepartID: itemID,
		ItemID:   itemID,
		Result:   result,
		Doctor:   "某某",
	}
	b, err := json.Marshal(testVal)
	if err != nil {
		fmt.Println("json.Marshal ", err.Error())
		t.FailNow()
	}
	checkInvoke(t, stub, [][]byte{[]byte("putData"), b})
}

func addIndex(t *testing.T, stub *shim.MockStub, itemID string) {
	testVal := uicc.Index{
		InstID:   "1001",
		DepartID: itemID,
		ItemID:   itemID,
	}
	b, err := json.Marshal(testVal)
	if err != nil {
		fmt.Println("json.Marshal ", err.Error())
		t.FailNow()
	}
	checkInvoke(t, stub, [][]byte{[]byte("putIndex"), b})
}

type inviteRet struct {
	Ret      string
	InviteID string
}

type getListRet struct {
	Ret  string
	Data []RetInviteItem
}

func TestExample02_Invoke(t *testing.T) {
	scc := new(DRCChaincode)
	stub := shim.NewMockStub("drcc", scc)
	checkInit(t, stub, [][]byte{[]byte("init")})

	uicc := new(uicc.UICChaincode)
	uicStub := shim.NewMockStub("uicc", uicc)
	stub.MockPeerChaincode("uicc", uicStub)
	checkInit(t, uicStub, [][]byte{[]byte("init")})

	hdcc := new(hdcc.HDCChaincode)
	hdcStub := shim.NewMockStub("hdcc", hdcc)
	uicStub.MockPeerChaincode("hdcc", hdcStub)
	checkInit(t, hdcStub, [][]byte{[]byte("init")})

	inviteArgs := InviteArgs{
		UserID:    "1",
		Cert:      "Cert",
		Secret:    "SECRET",
		Callback:  "http://39.104.99.78:8666/client/member/invitation/result/list?invitationCode=1",
		DUName:    "zhan",
		PatternID: "6789",
	}
	b := addInvite(t, stub, inviteArgs)
	ivtRet := inviteRet{}
	err := json.Unmarshal(b, &ivtRet)
	if err != nil {
		fmt.Println("json.Unmarshal ", err.Error())
		t.FailNow()
	}
	key, err := stub.CreateCompositeKey("ivt", []string{inviteArgs.UserID, ivtRet.InviteID})
	inviteArgs.Status = 1
	inviteArgs.Date = time.Now().Format("2006/01/02")
	b, err = json.Marshal(inviteArgs)
	checkState(t, stub, key, string(b))

	b = checkRet(t, stub, [][]byte{[]byte("getInviteList")})
	fmt.Println("getInviteList ret : ", string(b))
	listRet := getListRet{}
	err = json.Unmarshal(b, &listRet)
	if err != nil {
		fmt.Println("json.Unmarshal ", err.Error())
		t.FailNow()
	}

	addHealthData(t, hdcStub, "2345", "3456")
	addHealthData(t, hdcStub, "2346", "未见异常")
	addIndex(t, uicStub, "2345")
	addIndex(t, uicStub, "2346")

	acceptArg := AcceptArgs{
		InviteID: ivtRet.InviteID,
	}
	b, err = json.Marshal(acceptArg)
	checkInvoke(t, stub, [][]byte{[]byte("accept"), b})
}
