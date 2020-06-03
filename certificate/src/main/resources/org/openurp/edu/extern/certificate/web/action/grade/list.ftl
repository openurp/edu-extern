[#ftl]
[@b.head/]
[@b.grid items=certificateGrades var="certificateGrade"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}",action.add());
      bar.addItem("${b.text("action.modify")}",action.edit());
      bar.addItem("成绩认定", action.single("convertList"), "action-update");
      bar.addItem("${b.text("action.delete")}",action.remove());

      var bar1=bar.addMenu("导入导出");
      bar1.addItem("导入","importForm()");
      bar1.addItem("导出","exportData()");
      bar1.addItem("学分银行", action.exportData("info.std.user.name:姓名,info.std.person.code:身份证号,info.subject.code:转换学校代码,original.course.code:原课程来源代码,original.major.name:原专业名称,info.subject.name:原课程名称,original.school.name:原办学机构,original.level.code:原教育层次代码,original.project.category.code:原教育类别代码,original.course.credits:原学分,original.course.creditHours:原学时,courseGrade.scoreText:原成绩,info.acquiredOn:获得时间,info.std.level.code:转换后教育层次代码,courseGrade.course.code:转换后课程代码,courseGrade.course.name:转换后课程名称,courseGrade.course.credits:转换后学分", "xls", "fileName=批次学分银行成绩-证书成绩&dataInSource=courseGrade"), "excel.png");
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.user.code" title="学号" width="13%"/]
        [@b.col property="std.user.name" title="姓名" width="11%"/]
        [@b.col property="subject.name" title="考试科目" width="25%"/]
        [@b.col property="scoreText" title="成绩" width="7%"]
          <span [#if !(certificateGrade.passed)]style="color:red"[/#if]>
            ${(certificateGrade.scoreText)!}
          </span>
        [/@]
        [@b.col property="std.state.department.name" title="院系"  width="15%"/]
        [@b.col property="certificate" title="证书编号"  width="15%"]${(certificateGrade.certificate)!"--"}[/@]
        [@b.col property="updatedAt" title="录入时间"  width="12%"]${(certificateGrade.updatedAt?string("yy-MM-dd HH:mm"))!"--"}[/@]
        [@b.col title="免修" sortable="false"  width="40px"][#if certificateGrade.courses?size>0]${certificateGrade.courses?size}[/#if][/@]
    [/@]
[/@]

[@b.form name="certificateGradeListForm" target="certificateGradeList" action="!index"]
        <input type="hidden" name="configId" id="configId" />
        <input type="hidden" name="params" value="${b.paramstring}" />
[/@]

<script>
    //导出校外考试成绩
    function exportData(){
        var certificateGradeIds = bg.input.getCheckBoxValues("certificateGrade.id");
        var form = action.getForm();
        if (certificateGradeIds) {
            bg.form.addInput(form,"certificateGradeIds",certificateGradeIds);
        }else{
            if(!confirm("是否导出查询条件内的所有数据?")) return;
                if(""!=action.page.paramstr){
                  bg.form.addHiddens(form,action.page.paramstr);
                  bg.form.addParamsInput(form,action.page.paramstr);
                }
            bg.form.addInput(form,"certificateGradeIds","");
        }
        bg.form.addInput(form,"certificateGradeIds",bg.input.getCheckBoxValues("certificateGrade.id"));
        bg.form.addInput(form,"keys","std.user.code,std.name,subject.category.name,subject.name,score,scoreText,std.department.name,std.major.name,std.grade,certificate,acquiredOn,examNo,updatedAt,courseGradeSize");
        bg.form.addInput(form,"titles","学号,姓名,考试类型,考试科目,分数,成绩,院系,专业,年级,证书编号,考试日期,准考证号,录入时间,已认定课数");
        bg.form.addInput(form,"fileName","校外考试成绩数据");
        bg.form.submit(form,"${b.url('!export')}","_self");
    }
</script>
[@b.foot/]
