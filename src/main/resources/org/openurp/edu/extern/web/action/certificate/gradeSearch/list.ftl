[#ftl]
[@b.head/]
[@b.grid items=certificateGrades var="certificateGrade"]
    [@b.gridbar]
      bar.addItem("${b.text('action.export')}",
           action.exportData("std.code:学号,std.name:姓名,certificate.name:证书,scoreText:成绩,"+
                             "std.state.department.name:院系,std.state.major.name:专业,std.state.grade.code:年级,certificate:证书编号,acquiredOn:考试日期",null,'fileName=证书成绩'));
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.code" title="学号" width="13%"/]
        [@b.col property="std.name" title="姓名" width="11%"/]
        [@b.col property="certificate.name" title="证书名称" /]
        [@b.col property="scoreText" title="成绩" width="7%"]
          <span [#if !(certificateGrade.passed)]style="color:red"[/#if]>
            ${(certificateGrade.scoreText)!}
          </span>
        [/@]
        [@b.col property="std.state.department.name" title="院系"  width="15%"/]
        [@b.col property="certificateNo" title="证书编号"  width="15%"]${(certificateGrade.certificateNo)!"--"}[/@]
        [@b.col property="acquiredOn" title="获得年月" width="12%"]${(certificateGrade.acquiredOn?string("yy-MM"))!"--"}[/@]
    [/@]
[/@]
[@b.foot/]
