package hdcc

import (
	"encoding/json"
	"fmt"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// HDCChaincode example simple Chaincode implementation
type HDCChaincode struct {
}

// HealthData implementation
type HealthData struct {
	UserID   string
	DepartID string
	ItemID   string
	Result   string
	Doctor   string
}

// InputArgs implementation
type InputArgs struct {
	DepartID string
	ItemID   string
	Date     string
}

func (t *HDCChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ex02 Init")

	stub.PutState("departs_info", []byte("[{\"DepartName\":\"血糖\", \"DepartID\":\"2345\"}, {\"DepartName\":\"血脂\", \"DepartID\":\"2347\"}, {\"DepartName\":\"血压\", \"DepartID\":\"2349\"}]"))

	return shim.Success(nil)
}

func (t *HDCChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("hdcc Invoke")
	function, args := stub.GetFunctionAndParameters()
	if function == "getDeparts" {
		return t.getDeparts(stub, args)
	} else if function == "putData" {
		return t.putData(stub, args)
	} else if function == "getData" {
		return t.getData(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"getDeparts\" \"putData\" \"getData\"")
}

func (t *HDCChaincode) getDeparts(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err error
	var ret string
	//{"sex":"1", "age":"30"} //性别1男2女

	Avalbytes, err := stub.GetState("departs_info")
	if err != nil {
		return shim.Error(err.Error())
	}

	ret = "{\"Ret\":\"ok\", \"Data\":" + string(Avalbytes[:]) + "}"

	return shim.Success([]byte(ret))
}

func (t *HDCChaincode) putData(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//{"userID":"4567", "departID":"1234", "itemID":"2345", "result":"3456", "doctor":"某某"}
	fmt.Println("put data : ", string(args[0]))
	var err error
	hd := &HealthData{}
	err = json.Unmarshal([]byte(args[0]), &hd)
	if err != nil {
		return shim.Error(err.Error())
	}

	key, err := stub.CreateCompositeKey("hd", []string{hd.UserID, hd.ItemID, time.Now().Format("2006/01/02")})
	if err != nil {
		return shim.Error(err.Error())
	}

	val, err := json.Marshal(hd)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(key, val)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte("{\"Ret\":\"ok\"}"))
}

func (t *HDCChaincode) getData(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//{"departID":"1234", "itemID":"2345", "date":"2018/08/31"}
	var err error
	input := &InputArgs{}
	err = json.Unmarshal([]byte(args[0]), &input)
	if err != nil {
		fmt.Println()
		return shim.Error("json.Unmarshal " + err.Error())
	}

	//todo : get uid from abac
	uid := "4567"

	iter, err := stub.GetStateByPartialCompositeKey("hd", []string{uid, input.ItemID, input.Date})
	if err != nil {
		return shim.Error("stub.GetStateByPartialCompositeKey " + err.Error())
	}
	defer iter.Close()

	var result string
	for iter.HasNext() {
		val, err := iter.Next()
		if err != nil {
			return shim.Error("iter.Next " + err.Error())
		}
		hd := &HealthData{}
		err = json.Unmarshal(val.GetValue(), &hd)
		if err != nil {
			return shim.Error("json.Unmarshal2 " + err.Error())
		}
		result = hd.Result
		break
	}

	// if result == "" {
	// 	//return shim.Error("No valid value found!")
	// 	result = ""
	// }

	// result = strings.Replace(result, "\"", "\\\"", -1)

	return shim.Success([]byte("{\"Ret\":\"ok\", \"Result\":\"" + result + "\"}"))
}
