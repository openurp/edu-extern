[#ftl/]
[@b.head/]
[@b.form name="signupListForm" action="!search" target="signupList"]
    [@b.grid items=signups var="signup"]
        [#if !info?exists]
        [@b.gridbar]
          m=bar.addMenu("${b.text("action.export")}","exportList(document.signupsearchForm)");
          m.addItem("四六级格式","exportCET(document.signupsearchForm)");
          m.addItem("普通话格式","exportPTH(document.signupsearchForm)");
          m.addItem("计算机格式","exportComputer(document.signupsearchForm)");
          m.addItem("PETS","exportPETS(document.signupsearchForm)");
          bar.addItem("设置考场","batchUpdateExamRoom()");
          function batchUpdateExamRoom(){
             var examRoom = prompt("考场名称：");
             if(examRoom){
               var form = action.getForm();
               bg.form.addInput(form,"examRoom",examRoom);
               action.submitIdAction('batchUpdateExamRoom',true);
             }
          }
        [/@]
        [/#if]
        [@b.row]
            [@b.boxcol/]
            [@b.col property="std.code" title="学号" width="11%"/]
            [@b.col property="std.name" title="姓名" width="10%"/]
            [@b.col property="std.state.grade" title="年级" width="7%"/]
            [@b.col property="std.state.department.name" title="院系" width="15%"/]
            [@b.col property="subject.category.name" title="考试类型" width="9%"/]
            [@b.col property="subject.name" title="报名科目" width="14%"/]
            [@b.col property="examRoom" title="考场"/]
            [@b.col property="seatNo" title="座位号" width="5%"][#if signup.seatNo>0]${signup.seatNo}[/#if]&nbsp;[/@]
            [@b.col property="updatedAt" title="报名时间" width="10%"] <span title="${signup.ip!}">${((signup.updatedAt)?string("MM-dd HH:mm"))!}</span>[/@]
        [/@]
    [/@]
[/@]
<script>
    var form = document.signupListForm;

    function exportList(form){
        bg.form.addInput(form, "titles", "std.code:学号,std.name:姓名,std.state.grade.code:年级,std.gender.name:性别,std.state.major.name:专业,"+
                      "std.state.major.code:专业代码,std.state.department.name:所属院系,subject.category.name:考试类型,"+
                      "subject.name:报名科目,semester.schoolYear:学年,semester.name:学期,feeOfSignup:报名费,"+
                      "feeOfMaterial:材料费,feeOfOutline:考纲费,total:合计,updatedAt:报名时间,campus.name:考试校区,examRoom:考场,seatNo:座位号");
        bg.form.addInput(form, "fileName", "校外考试报名数据");
        bg.form.submit(form, "${b.url('!exportData')}","_self");
    }

    function exportCET(form){
        bg.form.addInput(form, "keys", "subject.name,std.name,std.gender.name,std.code,std.person.idType.name,std.person.code,std.level.name,std.duration,std.beginOn,std.state.grade.code,std.state.department.name,std.state.major.name,std.squad.name,std.squad.code");
        bg.form.addInput(form, "titles", "报考科目,姓名,性别,学号,证件类型,证件号,培养层次,学制,入学年份,年级,院系,专业,班级名称,班级代码");
        bg.form.addInput(form, "fileName", "四六级报名数据");
        bg.form.submit(form, "${b.url('!exportData')}","_self");
    }

    function exportComputer(form){
        bg.form.addInput(form, "keys", "std.code,std.name,std.gender.name,std.person.idType.name,std.person.code,std.state.department.name,std.state.major.discipline.name,std.state.major.name,std.beginOn,std.duration,std.squad.name,subject.name,payState.name");
        bg.form.addInput(form, "titles", "学号,姓名,性别,证件类型,证件号码,学院名称,学科名称,专业名称,入学年份,学制,班级名称,报名科目,缴费状态");
        bg.form.addInput(form, "fileName", "计算机报名数据");
        bg.form.submit(form, "${b.url('!exportData')}","_self");
    }

    function exportPTH(form){
        bg.form.addInput(form, "keys", "std.code,std.name,std.state.grade.code,std.gender.name,std.state.department.name");
        bg.form.addInput(form, "titles", "学号,姓名,年级,性别,院系");
        bg.form.addInput(form, "fileName", "普通话报名数据");
        bg.form.submit(form, "${b.url('!exportData')}","_self");
    }

    function exportPETS(form){
        bg.form.addInput(form, "keys", "std.project.category.name,std.person.idType.name,std.person.code,std.name,std.person.gender.name,std.person.birthday,std.project.school.name,std.level.name,std.enrollYear,std.graduateStatus,dummy1,subject.name,std.examineeCode,dummy2,std.code,std.state.grade,std.state.department.name,updatedAt,ip");
        bg.form.addInput(form, "titles", "考生来源,证件类型,证件号码,姓名,性别,出生日期,所在学校,录取层次,入学年份,是否毕业,学籍或考籍信息变更情况,报考级别和科目,考生号,备注,学号,年级,院系,报名时间,报名IP");
        bg.form.addInput(form, "fileName", "PETS报名数据");
        bg.form.submit(form, "${b.url('!exportData')}","_self");
    }
</script>
[@b.foot/]
