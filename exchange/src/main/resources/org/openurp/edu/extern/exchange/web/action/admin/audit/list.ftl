[#ftl]
[@b.head/]
  [@b.grid items=exchangeStudents var="exchangeStudent"]
    [@b.gridbar]
      bar.addItem("审核...", action.single("info"));
      //bar.addItem("${b.text('action.export')}", "exportData()");
      function exportData(){
        var form = document.searchForm;
        bg.form.addInput(form, "keys", "std.user.code,std.user.name,fromGrade,fromDepart.name,fromMajor.name,fromDirection.name,fromSquad.name,toGrade,toDepart.name,toMajor.name,toDirection.name,toSquad.name,gpa,majorGpa,otherGpa,mobile,state");
        bg.form.addInput(form, "titles", "学号,姓名,转出年级,转出院系,转出专业,转出方向,转出班级,转入年级,转入院系,转入专业,转入方向,转入班级,总绩点,专业课成绩绩点,专业课外成绩绩点,联系电话,状态");
        bg.form.addInput(form, "fileName", "学生转专业申请名单");
        bg.form.submit(form, "${b.url('!export')}","_self");
      }
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.user.code" width="13%"/]
      [@b.col title="姓名" property="std.user.name" width="8%"/]
      [@b.col title="交流学校" property="school.name"  width="24%"]
       ${(exchangeStudent.school.name)}
      [/@]
      [@b.col title="学习专业" property="majorName" width="15%"/]
      [@b.col title="学习时间"  width="15%"]
        ${exchangeStudent.beginOn?string("yyyy-MM")}~${exchangeStudent.endOn?string("yyyy-MM")}
      [/@]
      [@b.col title="冲抵学分" width="10%" property="exemptionCredits"/]
      [@b.col title="状态" width="10%" property="auditState"/]
    [/@]
  [/@]
[@b.foot/]
