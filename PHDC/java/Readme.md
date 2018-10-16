# API DOC 1.0

## 客户端界面


### 预约
<pre>
POST /member/reserve
head:
param: {
	memberId:用户ID,
	dictionaryId: 项目ID,
	institutionId: 体检机构ID,
	name: 姓名,
	mobile: 手机号,
	gender: 性别 1)男 2)女,
	reserveTime: 预约时间 yyyy/MM/dd
}
resp:{
	code: 响应码,
	data: 预约ID,
	message: 响应说明
}
</pre>


### 到检
<pre>
POST /reserve/sign
param: {
	reserveId: 预约Id
}
resp:{
	code: 响应码,
	message: 响应说明
}
</pre>
### 根据预约ID查看结果

<pre>
GET /member/reserve/exam/result
head:
param: {
	id: 预约ID
}
resp:{
	code: 响应码,
	data: [
		{
			id: 结果ID,
			departId: 科室Id,
			dictionaryId:项目ID,
			checkDate: "检查日期",
			dictionaryName: "项目名称",
			result: "结果",
			summary: "结论",
			checkDoctor: "检查医生",
			departName:"科室"		
		}
	]
}
</pre>

## 健康数据机构端界面

### 获取机构的预约数据

<pre>
GET /institution/member/reserve/list
param:{
	institutionId: "机构ID",
	pageSize: 10*,
    pageNum: 1*
}
resp: {
	...,
    data: [
    	{
    		id: "预约ID",
        	name: "预约人姓名",
        	age: "年龄",
        	gender: 性别 1男2女,
        	reserveTime: "预约日期",
        	status: "预约状态 1)已预约 2）已到检 3）未到检 4) 已过期,"
        }
    ],
    ...
}
</pre>

### 修改预约
<pre>
POST /institution/member/reserve/status
param:{
	reserveId: 预约Id,
	status:  2）已到检 3）未到检',
}
resp: {
	...,
    ...
}
</pre>

### 获取所有机构下的用户

<pre>
GET /institution/member/list
param:{
	institutionId:机构ID
	pageSize: 10*,
    pageNum: 1*
}
resp: {
	...,
    data: [//
    	{
        	id: "用户ID",
            name: "用户姓名",
            gender: "性别 1男 2女",
            finalReserveTime: "最后检查时间"
        }
    ]
    ...
}
</pre>

### 获取该机构该用户下的体检数据
<pre>
GET /institution/member/exam/result/list
param:{
	institutionId: 机构ID,
	memberId: 用户ID,
	pageSize: 10*,
    pageNum: 1*
}
resp: {
	...,
    data: [//
    	{
			id: "结果ID",
        	checkDate: "检查日期",
        	dictionaryName: "检查项名称",
        	resule: "检查结果",
        	summary: "结论",
        	departDoctor: "科室医生",
        	checkDoctoer: "检查医生"
        }
    ],
    ...
}
</pre>

### 获取该机构下的用户体检数据
<pre>
GET /institution/member/exam/list
param:{
	institutionId: 机构ID,
	pageSize: 10*,
    pageNum: 1*
}
resp: {
	...,
    data: [//
    	{
			id: "数据ID",
            age: "年龄",
            gender: "1男 2女",
            dictionaryName: "检查项名称",
        	resule: "检查结果",*
        	summary: "结论",*
        	departDoctor: "科室医生",*
        	checkDoctoer: "检查医生"*
            status: "状态 1)未出结果 2)已出结果"
        }
    ],
    ...
}
</pre>

### 输入体检结果结论
<pre>
POST /institution/member/exam/status
param: {
	resultId: 结论Id,
	resuleSumary: "结果/结论",
    departCheckDoctor: "检查/科室医生"
}
resp: {
	...,
    ...
}
</pre>

## 数据用户端

### 获取申请用户数据列表
<pre>
GET /client/member/invitation/list
param:{
	institutionId:机构ID
	pageSize: 10*,
    pageNum: 1*
}
resp: {
	...,
    data:[//pageinfo
    	{
        	invitationId: "邀请ID",
        	memberId: "用户ID",
            name: "姓名",
            status: 1)已生成 2)已申请等待批准 3)拒绝申请 4)同意申请 5)已过期,
            invitationTime: "邀请时间"
        }
    ]
    ...
}
</pre>

### 申请用户数据访问
<pre>
POST /client/member/invitation/access
param: {
	memberId:保险公司之类的用户ID,
	name: "用户姓名",
    mobile: "用户手机号",
    invitationCode: "邀请ID",
    invitationReportId: "申请的访问内容ID"
}
resp: {
	...,
    ...
}
</pre>

### 查看申请用户数据

<pre>
GET /client/member/invitation/detail
param: {
	memberId: 保险公司之类的用户ID
	id: "邀请ID",
    pageSize: 10*,
    pageNum: 1*
}
resp: {
	...,
    data: {
    	name: "用户名",
        mobile: "手机号",
        invitationTime: "邀请时间",
        //pageInfo:[
        	{
            	checkDate: "检查日期",
        		dictionaryName: "检查项目",
        		result: "检查结果"
            }
        ]
    }
    ...
}
</pre>

### 记录用户数据
<pre>
POST /client/member/invitation/result/list
head: {
	token: "访问令牌"
}
param: { //application/json
	id: "邀请ID",
	resultIds: [
		ID
	]
}
resp: {
	...,
	...
}
</pre>