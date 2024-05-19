[#ftl]
[@b.head/]
  [@b.toolbar title="校外学习经历"]
    bar.addBack();
  [/@]
  [@b.form name="externStudentForm" action="!save" theme="list"]
    [#if (externStudent.id)?exists]
      [@b.field label="学号"]<span style="display: inline-block;">${(externStudent.std.code)!} ${(externStudent.std.name)!}[/@]
    [#else]
      [@b.field label="学号"]
       <input name="stdCode" style="width:200px" placeholder="输入学号后，点击页面空白处，即可获取该学生信息">
       <input type="hidden" id="stdId" name="externStudent.std.id" value="${(externStudent.std.id)!}"/>
       <span id="stdName"></span>
      [/@]
    [/#if]
    [@b.select label="外校名称" name="externStudent.school.id" items=schools?sort_by("name") empty="..." value=(externStudent.school.id)!/]
    [@b.select label="培养层次" name="externStudent.level.id" items=levels required="true" value=(externStudent.level.id)!/]
    [@b.select label="教育类别" name="externStudent.category.id" items=categories required="true" value=(externStudent.category.id)! /]
    [@b.textfield label="外校专业" name="externStudent.majorName" value=(externStudent.majorName)! required="true" maxlength="100"/]
    [@b.startend label="就读时间" name="externStudent.beginOn,externStudent.endOn" start=(externStudent.beginOn)! end=(externStudent.endOn)! required="true"/]
    [@b.formfoot]
      <input type="hidden" name="externStudent.id" value="${(externStudent.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
[#if !(externStudent.id)?exists]
  <script>
    $(function() {
      function init(form) {
        var formObj = $(form);
        var stdNameObj = formObj.find("#stdName");

        formObj.find("[name=stdCode]").blur(function() {
          var thisObj = $(this);
          thisObj.parent().find(".error").remove();
          thisObj.parent().next().find(".error").remove();
          stdNameObj.empty();
          var code = thisObj.val().trim();
          if (code.length == 0) {
            throwError(thisObj.parent(), "请输入一个有效的学号");
            stdNameObj.html("<br>");
          } else {
            $.ajax({
              "type": "POST",
              "url": "${b.url("!loadStudent")}.json",
              "async": false,
              "dataType": "json",
              "data": {
                "q": code
              },
              "success": function(data) {
                  var dataObj = eval(data);
                  if(dataObj.length>0){
                    $("#stdId").parent().find(".error").remove();
                    $("#stdName").html(dataObj[0].name);
                    $("#stdId").val(dataObj[0].id);
                  }else{
                    throwError(thisObj.parent(), "请输入一个有效的学号");
                  }
              }
            });
          }
        });

        formObj.find(":submit").click(function() {
          var errObj = formObj.find("[name=stdCode]").parent().find(".error");
          formObj.find("[name=stdCode]").parent().append(errObj);
        });
      }

      function throwError(parentObj, msg) {
        var errObj = parentObj.find(".error");
          errObj = $("<label>");
          errObj.addClass("error");
          parentObj.append(errObj);
        errObj.text(msg);
      }

      $(document).ready(function() {
        init(document.externStudentForm);
      });
    });
  </script>
  [/#if]
[@b.foot/]
