/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package hdcc

import (
	"encoding/json"
	"fmt"
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
	bytes, err := stub.GetState(name)
	if err != nil {
		fmt.Println("State", err.Error())
		t.FailNow()
	}
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
	res := stub.MockInvoke("1", [][]byte{[]byte("getData"), []byte(name)})
	if res.Status != shim.OK {
		fmt.Println("Query", name, "failed", string(res.Message))
		t.FailNow()
	}
	if res.Payload == nil {
		fmt.Println("Query", name, "failed to get value")
		t.FailNow()
	}
	if string(res.Payload) != value {
		fmt.Println("Query value", string(res.Payload), "was not", value, "as expected")
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

func TestExample02_Invoke(t *testing.T) {
	scc := new(HDCChaincode)
	stub := shim.NewMockStub("ex02", scc)

	// Init A=567 B=678
	checkInit(t, stub, [][]byte{[]byte("init")})

	testVal := HealthData{
		UserID:   "4567",
		DepartID: "2345",
		ItemID:   "2345",
		Result:   "3456",
		Doctor:   "某某",
	}
	b, err := json.Marshal(testVal)
	if err != nil {
		fmt.Println("json.Marshal ", err.Error())
		t.FailNow()
	}
	checkInvoke(t, stub, [][]byte{[]byte("putData"), b})
	key, err := stub.CreateCompositeKey("hd", []string{testVal.UserID, testVal.ItemID, time.Now().Format("2006/01/02")})
	if err != nil {
		fmt.Println("CreateCompositeKey ", err.Error())
		t.FailNow()
	}
	checkState(t, stub, key, string(b))

	date := time.Now().Format("2006/01/02")
	input := InputArgs{
		DepartID: "1234",
		ItemID:   "2345",
		Date:     date,
	}
	b, err = json.Marshal(input)
	if err != nil {
		fmt.Println("json.Marshal ", err.Error())
		t.FailNow()
	}
	fmt.Println(string(b))
	checkQuery(t, stub, string(b), "{\"Ret\":\"ok\", \"Result\":\"3456\"}")
}
