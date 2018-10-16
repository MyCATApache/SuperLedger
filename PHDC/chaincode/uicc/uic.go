package uicc

import (
	"encoding/json"
	"errors"
	"fmt"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// UICChaincode example simple Chaincode implementation
type UICChaincode struct {
	channelName string
}

// Init imlementation
func (t *UICChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ex02 Init")

	stub.PutState("ho_info", []byte("[{\"InstName\":\"体检机构1\", \"InstID\":\"1001\", \"Url\":\"预约访问地址\", \"PeerAddress\":\"peer节点地址\", \"Channel\":\"hdc\"}]"))
	// IMPORTANT : Comment out this line during unit testing
	t.channelName = "hdc"

	return shim.Success(nil)
}

// Invoke imlementation
func (t *UICChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("uicc Invoke")
	function, args := stub.GetFunctionAndParameters()
	if function == "getHO" {
		return t.getHO(stub, args)
	} else if function == "putIndex" {
		return t.putIndex(stub, args)
	} else if function == "getDataList" {
		return t.getDataList(stub, args)
	} else if function == "getDataByPattern" {
		return t.getDataByPattern(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"getHO\" \"putIndex\" \"getDataList\" \"getDataByPattern\"")
}

func (t *UICChaincode) getHO(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err error
	var ret string

	Avalbytes, err := stub.GetState("ho_info")
	if err != nil {
		return shim.Error(err.Error())
	}

	ret = "{\"Ret\":\"ok\", \"Data\":" + string(Avalbytes[:]) + "}"

	return shim.Success([]byte(ret))
}

type Index struct {
	InstID   string
	DepartID string
	ItemID   string
}

func (t *UICChaincode) putIndex(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//{"instID":"机构名称", "departID":"1234", "itemID":"2345"}
	idx := &Index{}
	err := json.Unmarshal([]byte(args[0]), &idx)
	if err != nil {
		return shim.Error(err.Error())
	}

	//todo : get uid from abac
	uid := "4567"
	date := time.Now().Format("2006/01/02")

	key, err := stub.CreateCompositeKey("idx", []string{uid, idx.ItemID, date})
	if err != nil {
		return shim.Error("stub.CreateCompositeKey " + err.Error())
	}

	b, err := json.Marshal(idx)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(key, b)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte("{\"Ret\":\"ok\"}"))
}

type InputArgs struct {
	DepartID string
	ItemID   string
	Date     string
}
type HORetResult struct {
	Ret    string
	Result string
}
type ResultDataItem struct {
	InstName   string
	ItemName   string
	Result     string
	Conclusion string
	Date       string
}

func (t *UICChaincode) getDataList(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//todo : get uid from abac
	uid := "4567"
	iter, err := stub.GetStateByPartialCompositeKey("idx", []string{uid})
	if err != nil {
		return shim.Error(err.Error())
	}
	defer iter.Close()

	var result string

	for iter.HasNext() {
		idx, err := iter.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		_, keys, err := stub.SplitCompositeKey(idx.GetKey())
		if err != nil {
			return shim.Error(err.Error())
		}
		val := &Index{}
		err = json.Unmarshal(idx.GetValue(), &val)
		if err != nil {
			return shim.Error(err.Error())
		}
		tempID, err := strconv.Atoi(val.ItemID)
		if err != nil {
			return shim.Error(err.Error())
		}
		if tempID&0x01 == 0 {
			continue
		}
		retResult, err := t.getData(stub, val.DepartID, val.ItemID, keys[2])
		if err != nil {
			return shim.Error(err.Error())
		}

		tempStr := strconv.Itoa(tempID + 1)
		conclusion, err := t.getData(stub, tempStr, tempStr, keys[2])
		if err != nil {
			return shim.Error(err.Error())
		}

		item := &ResultDataItem{}
		//todo : get InstName and ItemName from chain
		if val.InstID == "1001" {
			item.InstName = "体检机构1"
		} else if val.InstID == "1002" {
			item.InstName = "医院1"
		}
		if val.ItemID == "2345" {
			item.ItemName = "血糖"
		} else if val.ItemID == "2347" {
			item.ItemName = "血脂"
		} else if val.ItemID == "2349" {
			item.ItemName = "血压"
		} else {
			return shim.Error("Invalid ItemID : " + val.ItemID)
		}

		item.Result = retResult
		item.Date = keys[2]
		// todo : get Conclusion from chain
		item.Conclusion = conclusion
		if err != nil {
			return shim.Error(err.Error())
		}
		resultBytes, err := json.Marshal(item)
		if err != nil {
			return shim.Error(err.Error())
		}
		if result != "" {
			result += ", "
		}
		result += string(resultBytes)
		// result += string(idx.GetValue())
	}

	//result = strings.Replace(result, "\"", "\\\"", -1)

	return shim.Success([]byte("{\"Ret\":\"ok\", \"Data\":[" + result + "]}"))
}

type PatternArg struct {
	PatternID string
}

type PatternResult struct {
	Date       string
	DepartName string
	Result     string
}

func (t *UICChaincode) getDataByPattern(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//{"patternID":"6789"}
	//todo : get uid from abac
	uid := "4567"
	ptn := &PatternArg{}
	err := json.Unmarshal([]byte(args[0]), &ptn)
	if err != nil {
		return shim.Error(err.Error())
	}

	var list []string
	if ptn.PatternID == "6789" {
		list = []string{"2346", "2348", "2350"}
	} else if ptn.PatternID == "6790" {
		list = []string{"2346"}
	} else if ptn.PatternID == "6791" {
		list = []string{"2348"}
	} else if ptn.PatternID == "6792" {
		list = []string{"2350"}
	} else {
		return shim.Error("Invalid PatternID")
	}

	var resultList []PatternResult
	for _, itemID := range list {
		fmt.Println(itemID)

		iter, err := stub.GetStateByPartialCompositeKey("idx", []string{uid, itemID})
		if err != nil {
			return shim.Error(err.Error())
		}
		defer iter.Close()

		for iter.HasNext() {
			item, err := iter.Next()
			if err != nil {
				return shim.Error(err.Error())
			}
			fmt.Println("getDataByPattern item key : ", item.GetKey())
			_, keys, err := stub.SplitCompositeKey(item.GetKey())
			if err != nil {
				return shim.Error(err.Error())
			}
			pResult := PatternResult{}
			pResult.Date = keys[2]
			if itemID == "2346" {
				pResult.DepartName = "血糖"
			} else if itemID == "2348" {
				pResult.DepartName = "血脂"
			} else if itemID == "2350" {
				pResult.DepartName = "血压"
			} else {
				return shim.Error("Invalid ItemID : " + itemID)
			}
			idx := &Index{}
			err = json.Unmarshal(item.GetValue(), idx)
			resultStr, err := t.getData(stub, idx.DepartID, idx.ItemID, pResult.Date)
			if err != nil {
				return shim.Error(err.Error())
			}

			pResult.Result = resultStr
			resultList = append(resultList, pResult)
		}
	}
	retBytes, err := json.Marshal(resultList)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte("{\"Ret\":\"ok\", \"Data\":" + string(retBytes) + "}"))
}

func (t *UICChaincode) getData(stub shim.ChaincodeStubInterface, departID string, itemID string, date string) (string, error) {
	input := &InputArgs{
		DepartID: departID,
		ItemID:   itemID,
		Date:     date,
	}
	b, err := json.Marshal(input)
	if err != nil {
		return "", err
	}
	fmt.Println("Input Args : ", string(b))
	response := stub.InvokeChaincode("hdcc", [][]byte{[]byte("getData"), b}, t.channelName)
	if response.GetStatus() != shim.OK {
		errStr := "msg : " + response.GetMessage() + " | " + string(response.GetPayload())
		fmt.Println(errStr)
		return "", errors.New(errStr)
	}
	ret := &HORetResult{}
	err = json.Unmarshal(response.GetPayload(), &ret)
	if err != nil {
		return "", err
	}
	if ret.Ret != "ok" {
		return "", errors.New("Invalid Ret value")
	}
	return ret.Result, nil
}
