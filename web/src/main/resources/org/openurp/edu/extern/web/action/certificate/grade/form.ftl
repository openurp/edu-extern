[#ftl]
[@b.head/]
[@b.toolbar title="证书成绩维护"]
    bar.addBack("${b.text("action.back")}");
[/@]
[@b.form name="certificateGradeForm" action="!save" theme="list"]
    [#if (certificateGrade.std.id)?exists]
        [@b.field label="学号"]${(certificateGrade.std.code)}[/@]
    [#else]
        [@b.field name="findstudent"]
            [@b.textfield  theme="html" name="certificateGrade.std.code" maxlength="15"  id="stdCode" label="学号" value="${(certificateGrade.std.code)!}" required="true" style="width:150px" comment="<input type='button' value='查询' onClick='searchStudent()'/>"/]
            <input type="hidden" id="stdId" name="certificateGrade.std.id" value="${(certificateGrade.std.id)!}"/>
        [/@]
    [/#if]
    [@b.field label="姓名"]
      <table>
       <tr>
            <td id="stdName">${(certificateGrade.std.name)?default("&nbsp;")}</td>
       </tr>
      </table>
    [/@]
    [@b.select name="certificateGrade.certificate.id" items=certificates?sort_by("name") empty="..." required="true" label="证书名称" value=certificateGrade.certificate! style="width:150px"/]
    [@b.textfield name="certificateGrade.subject" maxlength="25" label="证书内课程"   value=certificateGrade.subject!/]
    [@b.select name="certificateGrade.gradingMode.id" items=gradingModes label="记录方式" empty="..." required="true" value=(certificateGrade.gradingMode.id)! style="width:150px"/]
    [@b.select name="certificateGrade.examStatus.id" items=examStatuses label="考试情况" required="true" value=(certificateGrade.examStatus.id)! style="width:150px"/]
    [@b.textfield name="certificateGrade.scoreText" maxlength="5" label="成绩"  onchange="setScore(this.value)" value=(certificateGrade.scoreText)! required="true" style="width:150px"/]
    [@b.textfield name="certificateGrade.score" maxlength="5" label="分数" check="match('number')" value=(certificateGrade.score)! style="width:150px" comment="须数字"/]
    [@b.radios name="certificateGrade.passed" items={"1":"合格", "0":"不合格"} label="是否合格" value=(certificateGrade.passed)!true?string("1", "0")/]
    [@b.date label="考试日期" name="certificateGrade.acquiredOn" value=(certificateGrade.acquiredOn)! required="true"/]
    [@b.textfield name="certificateGrade.examNo" label="准考证号码" value=(certificateGrade.examNo)! style="width:150px"/]
    [@b.textfield name="certificateGrade.certificateNo" label="证书编号" value=(certificateGrade.certificateNo)! maxlength="100" style="width:150px"/]
    [@b.formfoot]
        <input type="hidden" name="certificateGrade.id" value="${(certificateGrade.id)!}" />
        <input type="hidden" name="_params" value="${b.paramstring}" />
        [@b.submit value="action.submit"/]
    [/@]
[/@]
<script language="javascript" >
    var form = document.certificateGradeForm;
    function searchStudent(){
        jQuery.post("manage!searchStudent.action",{studentCode:$("#stdCode").val()},function(data){
            if (data == ""){
                $("#stdId").val("");
                $("#stdId").parent().find(".error").remove();
                $("#stdId").parent().append($("<label class='error' for='stdId'>查无此人!</label>"));
                $("#stdName").html("&nbsp;");
            } else {
                var dataObj = eval("(" + data + ")");
                $("#stdId").parent().find(".error").remove();
                $("#stdName").html(dataObj.student.name);
                $("#stdId").val(dataObj.student.id);
            }
        },"text");
    }

    function setScore(v){
      if(!isNaN(Number.parseFloat(v))){
        form['certificateGrade.score'].value=v;
      }
    }
</script>
[@b.foot/]
