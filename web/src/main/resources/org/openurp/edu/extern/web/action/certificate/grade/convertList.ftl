[#ftl]
[@b.head/]
  [@b.toolbar title = "<span style=\"color:blue\">" + grade.std.name + "（<span style=\"padding-left: 1px; padding-right: 1px\">" + grade.std.code + "</span>）于" + grade.acquiredOn?string("yyyy-MM-dd") +"获得“"+ grade.certificate.name + "”</span>课程及认定明细"]
    bar.addItem("返回", function() {
      bg.form.submit(document.searchForm);
    }, "backward.png");
  [/@]
  [#if convertedGrades?size>0]
  [@b.grid items=convertedGrades?sort_by(["course", "code"]) var="courseGrade" sortable="false"]
    [@b.gridbar]
      bg.form.addInput(action.getForm(), "grade.id", "${grade.id}");
      bar.addItem("取消", action.single("removeCourseGrade", "确认要取消冲抵吗？"), "action-edit-delete");
    [/@]
    [@b.row]
      [@b.boxcol type="radio"/]
      [@b.col title="课程代码" property="course.code"/]
      [@b.col title="课程名称" property="course.name" width="25%"/]
      [@b.col title="课程类别" property="courseType.name" width="15%"/]
      [@b.col title="学年学期" property="semester.code"]${courseGrade.semester.schoolYear} ${courseGrade.semester.name}[/@]
      [@b.col title="学分" property="course.defaultCredits"  width="6%"/]
      [@b.col title="成绩" property="score"  width="6%"]${(courseGrade.scoreText)!"--"}[/@]
      [@b.col title="绩点" property="gp"  width="6%"]${(courseGrade.gp?string("0.#"))!'--'}[/@]
      [@b.col title="修读类别" property="courseTakeType.name"/]
      [@b.col title="更新时间" property="updatedAt"  width="15%"]${courseGrade.updatedAt?string("yy-MM-dd HH:mm")}[/@]
    [/@]
  [/@]
  [/#if]
 [@b.toolbar title = "添加新的认定课程"]
    bar.addItem("认定", function() {
      var courseIds = "";
      var form = document.gradeDistributeForm;
      $(form).find("[name^=scoreText]").each(function() {
        if ($(this).val().trim().length) {
          courseIds += (courseIds.length > 0 ? "," : "") + $(this).prev().val();
        }
      });
      if(courseIds){
         bg.form.submit(form, "${b.url("!convert")}");
      }else{
         alert("尚未填写认定成绩");
      }
    }, "action-new");
  [/@]
  [@b.form name="gradeDistributeForm" action="!convert"]
  <input type="hidden" name="grade.id" value="${grade.id}"/>
  <div class="grid" style="border:0.5px solid #006CB2">
  <table class="gridtable">
    <thead class="gridhead">
      <tr>
        <th width="60px">序号</th>
        <th width="10%">课程代码</th>
        <th width="20%">课程名称</th>
        <th width="15%">课程类别</th>
        <th width="50px">学分</th>
        <th width="130px">开课学期</th>
        <th width="100px">记录方式</th>
        <th width="80px">成绩(分数)</th>
        <th width="60px">考核方式</th>
      </tr>
    </thead>
    <tbody>
      [#list planCourses?sort_by(["course","name"]) as planCourse]
      <tr class="${(0 == planCourse_index % 2)?string("griddata-even", "griddata-odd")}">
        <td>${planCourse_index+1}</td>
        <td>${planCourse.course.code}</td>
        <td>${planCourse.course.name}</td>
        <td>${planCourse.group.courseType.name}</td>
        <td>${planCourse.course.defaultCredits}</td>
        <td title="第${planCourse.terms}学期 ">
        [#assign hasSemester=false/]
        [#if semesters.get(planCourse)??]
        [#assign hasSemester=true/]
        ${semesters.get(planCourse).schoolYear} ${semesters.get(planCourse).name} 学期
        [/#if]
        </td>
        <td>
          <select name="gradingMode_${planCourse.course.id}" style="width: 100px" onchange="displayScore(this.value,${planCourse.course.id})">
            [#list gradingModes as gradingMode]
            <option value="${gradingMode.id}"[#if 1 == gradingMode.id] selected[/#if]>${gradingMode.name}</option>
            [/#list]
          </select>
        </td>
        <td>
          <input type="hidden" name="course.id" value="${planCourse.course.id}"/>
          <input type="hidden" name="courseType_${planCourse.course.id}" value="${planCourse.group.courseType.id}"/>
          [#if hasSemester]
          <input type="hidden" name="semester_${planCourse.course.id}" value="${semesters.get(planCourse).id}"/>
          <input type="text" name="scoreText_${planCourse.course.id}" value="" maxlength="5" style="width: 50px"/>
          <div id="score_${planCourse.course.id}" style="display:none">
            (<input type="text" name="score_${planCourse.course.id}" value="" maxlength="10" style="width: 50px"/>)
          </div>
          [/#if]
        </td>
        <td>${planCourse.course.examMode.name}</td>
      </tr>
      [/#list]
    </tbody>
  </table>
  </div>
  [/@]

  <script>
    var gradingModes={};
    [#list gradingModes as gradingMode]
      gradingModes['g${gradingMode.id}']=${gradingMode.numerical?string('1','0')}
    [/#list]
    function displayScore(gradingModeId,courseId){
      if(gradingModes['g'+gradingModeId]=='1'){
        document.getElementById('score'+courseId).style.display="none";
      }else{
        document.getElementById('score'+courseId).style.display="";
      }
    }
  </script>
[@b.foot/]
