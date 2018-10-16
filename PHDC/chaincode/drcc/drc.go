package drcc

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"phdc/uicc"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"github.com/sony/sonyflake"
)

// DRCChaincode example simple Chaincode implementation
type DRCChaincode struct {
	flake       *sonyflake.Sonyflake
	channelName string
}

type InviteArgs struct {
	UserID    string
	Cert      string
	Secret    string
	Callback  string
	DUName    string
	PatternID string
	Status    int
	Date      string
}

func (t *DRCChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ex02 Init")
	t.flake = sonyflake.NewSonyflake(sonyflake.Settings{})

	// IMPORTANT : Comment out this line during unit testing
	t.channelName = "uic"

	return shim.Success(nil)
}

func (t *DRCChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("drcc Invoke")
	function, args := stub.GetFunctionAndParameters()
	if function == "invite" {
		return t.invite(stub, args)
	} else if function == "getInviteList" {
		return t.getInviteList(stub, args)
	} else if function == "accept" {
		return t.accept(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"invite\" \"getInviteList\" \"accept\"")
}

func (t *DRCChaincode) invite(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	// {"userID":"4567", "cert":"用户公钥", "secret":"私钥加密后的密文", "callback":"邀请成功后的回调URL", "DUName":"机构名称", "patternID":"6789"}
	inviteArg := &InviteArgs{}
	err := json.Unmarshal([]byte(args[0]), &inviteArg)
	if err != nil {
		return shim.Error(err.Error())
	}

	id, err := t.flake.NextID()
	if err != nil {
		return shim.Error("ID generate fail")
	}
	fmt.Println("id : ", id)
	strID := strconv.FormatUint(id, 16)

	inviteArg.Date = time.Now().Format("2006/01/02")
	key, err := stub.CreateCompositeKey("ivt", []string{inviteArg.UserID, strID})
	if err != nil {
		return shim.Error(err.Error())
	}
	inviteArg.Status = 1 //状态1申请中2通过3拒绝

	b, err := json.Marshal(inviteArg)
	if err != nil {
		return shim.Error(err.Error())
	}
	// todo : encrypt b
	err = stub.PutState(key, b)
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.SetEvent("evtsender", []byte(strID))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte("{\"Ret\":\"ok\", \"InviteID\":\"" + strID + "\"}"))
}

type RetInviteItem struct {
	InviteID    string
	DUName      string
	PatternName string
	Status      int
	Date        string
}

func (t *DRCChaincode) getInviteList(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//todo : get uid from abac
	uid := "4567"

	iter, err := stub.GetStateByPartialCompositeKey("ivt", []string{uid})
	if err != nil {
		return shim.Error(err.Error())
	}
	defer iter.Close()

	var result []RetInviteItem
	for iter.HasNext() {
		item, err := iter.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		inviteArgs := &InviteArgs{}
		err = json.Unmarshal(item.GetValue(), inviteArgs)
		if err != nil {
			return shim.Error(err.Error())
		}

		fmt.Println("getDataByPattern item key : ", item.GetKey())
		_, keys, err := stub.SplitCompositeKey(item.GetKey())
		if err != nil {
			return shim.Error(err.Error())
		}
		ret := RetInviteItem{
			InviteID: keys[1],
			DUName:   inviteArgs.DUName,
			Status:   inviteArgs.Status,
			Date:     inviteArgs.Date,
		}

		if inviteArgs.PatternID == "6789" {
			ret.PatternName = "全年健康情况"
		} else if inviteArgs.PatternID == "6790" {
			ret.PatternName = "三年血糖情况"
		} else if inviteArgs.PatternID == "6791" {
			ret.PatternName = "三年血脂情况"
		} else if inviteArgs.PatternID == "6792" {
			ret.PatternName = "三年血压情况"
		} else {
			return shim.Error("Invalid PatternID")
		}
		result = append(result, ret)
	}

	b, err := json.Marshal(result)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte("{\"Ret\":\"ok\", \"Data\":" + string(b) + "}"))
}

type AcceptArgs struct {
	InviteID string
}

type dataResult struct {
	Ret  string
	Data []uicc.PatternResult
}

func (t *DRCChaincode) accept(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//{"inviteID":"5678"}
	//todo : get uid from abac
	uid := "4567"

	arg := &AcceptArgs{}
	err := json.Unmarshal([]byte(args[0]), &arg)
	if err != nil {
		return shim.Error(err.Error())
	}

	key, err := stub.CreateCompositeKey("ivt", []string{uid, arg.InviteID})
	if err != nil {
		return shim.Error(err.Error())
	}

	b, err := stub.GetState(key)
	if err != nil {
		return shim.Error(err.Error())
	}
	if b == nil {
		return shim.Error("Invalid InviteID")
	}

	inviteArgs := &InviteArgs{}
	err = json.Unmarshal(b, inviteArgs)
	if err != nil {
		return shim.Error(err.Error())
	}

	response := stub.InvokeChaincode("uicc", [][]byte{[]byte("getDataByPattern"), []byte("{\"patternID\":\"" + inviteArgs.PatternID + "\"}")}, "uic")
	if response.GetStatus() != shim.OK {
		errStr := "msg : " + response.GetMessage() + " | " + string(response.GetPayload())
		fmt.Println(errStr)
		return shim.Error(errStr)
	}

	dataRet := dataResult{}
	err = json.Unmarshal(response.GetPayload(), &dataRet)
	if err != nil {
		return shim.Error(err.Error())
	}

	dataBytes, err := json.Marshal(dataRet.Data)
	if err != nil {
		return shim.Error(err.Error())
	}

	// send response.Payload to callback
	reader := bytes.NewReader(dataBytes)
	url := inviteArgs.Callback
	request, err := http.NewRequest("POST", url, reader)
	if err != nil {
		return shim.Error(err.Error())
	}
	request.Header.Set("Content-Type", "application/json;charset=UTF-8")
	client := http.Client{}
	resp, err := client.Do(request)
	if err != nil {
		return shim.Error(err.Error())
	}
	respBytes, err := ioutil.ReadAll(resp.Body)
	restStr := strings.Replace(string(respBytes), "\"", "\\\"", -1)
	if err != nil {
		return shim.Error(err.Error())
	}

	fmt.Println("report : ", string(dataBytes))

	inviteArgs.Status = 2
	b, err = json.Marshal(inviteArgs)
	err = stub.PutState(key, b)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte("{\"Ret\":\"ok\", \"Msg\":\"" + restStr + "\"}"))
}
