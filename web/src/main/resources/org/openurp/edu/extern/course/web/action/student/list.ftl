[#ftl]
[@b.head/]
  [@b.grid items=externStudents var="externStudent"]
    [@b.gridbar]
      bar.addItem("新增",action.add());
      bar.addItem("修改",action.edit());
      bar.addItem("删除",action.remove());
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.user.code" width="13%"/]
      [@b.col title="姓名" property="std.user.name" width="8%"/]
      [@b.col title="学历层次" width="6%" property="level.name"/]
      [@b.col title="教育类别" width="9%" property="category.name"/]
      [@b.col title="学校" property="school.name"  width="23%"]
       ${(externStudent.school.name)}
      [/@]
      [@b.col title="学习专业" property="majorName" width="15%"/]
      [@b.col title="学习时间"  width="13%"]
        ${externStudent.beginOn?string("yyyy-MM")}~${externStudent.endOn?string("yyyy-MM")}
      [/@]
      [@b.col title="更新日期" width="8%" property="updatedAt"]${externStudent.updatedAt?string("MM-dd")}[/@]
    [/@]
  [/@]
[@b.foot/]
