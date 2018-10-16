/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package uicc

import (
	"encoding/json"
	"fmt"
	"phdc/hdcc"
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

func checkGetHO(t *testing.T, stub *shim.MockStub) {
	res := stub.MockInvoke("1", [][]byte{[]byte("getHO")})
	if res.Status != shim.OK {
		fmt.Println("Query getHO failed", string(res.Message))
		t.FailNow()
	}
	if res.Payload == nil {
		fmt.Println("Query getHO failed to get value")
		t.FailNow()
	}
}

func checkDataList(t *testing.T, stub *shim.MockStub, value string) {
	res := stub.MockInvoke("1", [][]byte{[]byte("getDataList")})
	if res.Status != shim.OK {
		fmt.Println("Query getDataList failed", string(res.Message))
		t.FailNow()
	}
	if res.Payload == nil {
		fmt.Println("Query getDataList failed to get value")
		t.FailNow()
	}
	if string(res.Payload) != value {
		fmt.Println("Query value", string(res.Payload), " was not", value, "as expected")
		t.FailNow()
	} else {
		fmt.Println(string(res.Payload))
	}
}

func checkInvoke(t *testing.T, stub *shim.MockStub, args [][]byte) {
	res := stub.MockInvoke("1", args)
	if res.Status != shim.OK {
		fmt.Println("Invoke", args, "failed", string(res.Message))
		t.FailNow()
	}
	fmt.Println("checkInvoke : ", string(res.GetPayload()))
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
	testVal := Index{
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

func TestExample02_getDataList(t *testing.T) {
	scc := new(UICChaincode)
	stub := shim.NewMockStub("uicc", scc)
	checkInit(t, stub, [][]byte{[]byte("init")})

	hdcc := new(hdcc.HDCChaincode)
	hdcStub := shim.NewMockStub("hdcc", hdcc)
	stub.MockPeerChaincode("hdcc", hdcStub)
	checkInit(t, hdcStub, [][]byte{[]byte("init")})

	addHealthData(t, hdcStub, "2345", "3456")
	addHealthData(t, hdcStub, "2346", "未见异常")
	addIndex(t, stub, "2345")
	addIndex(t, stub, "2346")

	testVal2 := ResultDataItem{
		InstName:   "体检机构1",
		ItemName:   "血糖",
		Result:     "3456",
		Conclusion: "未见异常",
		Date:       time.Now().Format("2006/01/02"),
	}
	b, err := json.Marshal(testVal2)
	if err != nil {
		fmt.Println("json.Marshal ", err.Error())
		t.FailNow()
	}
	target := "{\"Ret\":\"ok\", \"Data\":[" + string(b) + "]}"
	checkDataList(t, stub, target)

}

func TestExample02_getPattern(t *testing.T) {
	scc := new(UICChaincode)
	stub := shim.NewMockStub("uicc", scc)
	checkInit(t, stub, [][]byte{[]byte("init")})

	hdcc := new(hdcc.HDCChaincode)
	hdcStub := shim.NewMockStub("hdcc", hdcc)
	stub.MockPeerChaincode("hdcc", hdcStub)
	checkInit(t, hdcStub, [][]byte{[]byte("init")})

	addHealthData(t, hdcStub, "2345", "3456")
	addHealthData(t, hdcStub, "2346", "OK")

	addIndex(t, stub, "2345")
	addIndex(t, stub, "2346")

	p := &PatternArg{
		PatternID: "6790",
	}
	b, err := json.Marshal(p)
	if err != nil {
		fmt.Println("json.Marshal ", err.Error())
		t.FailNow()
	}
	checkInvoke(t, stub, [][]byte{[]byte("getDataByPattern"), b})
}

func TestExample02_Invoke(t *testing.T) {
	scc := new(UICChaincode)
	stub := shim.NewMockStub("uicc", scc)

	// Init A=567 B=678
	checkInit(t, stub, [][]byte{[]byte("init")})

	checkGetHO(t, stub)

	testVal := Index{
		InstID:   "5678",
		DepartID: "1234",
		ItemID:   "2345",
	}
	b, err := json.Marshal(testVal)
	if err != nil {
		fmt.Println("json.Marshal ", err.Error())
		t.FailNow()
	}
	checkInvoke(t, stub, [][]byte{[]byte("putIndex"), b})

	key, err := stub.CreateCompositeKey("idx", []string{"4567", testVal.ItemID, time.Now().Format("2006/01/02")})
	if err != nil {
		fmt.Println("CreateCompositeKey ", err.Error())
		t.FailNow()
	}
	checkState(t, stub, key, string(b))

}
