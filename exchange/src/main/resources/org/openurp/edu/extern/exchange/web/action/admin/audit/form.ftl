[#ftl]
[@b.head/]
  [@b.toolbar title="校外学习经历"]
    bar.addBack();
  [/@]
  [@b.form name="exchangeStudentForm" action="!save" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [#if (exchangeStudent.id)?exists]
      [@b.field label="学号"]<span style="display: inline-block;">${(exchangeStudent.std.user.code)!} ${(exchangeStudent.std.user.name)!}[/@]
    [#else]
      [@b.field label="学号"]
       <input name="stdCode" style=elementSTYLE placeholder="输入学号后，点击页面空白处，即可获取该学生信息">
       <input type="hidden" id="stdId" name="exchangeStudent.std.id" value="${(exchangeStudent.std.id)!}"/>
       <span id="stdName"></span>
      [/@]
    [/#if]
    [@b.select label="外校名称" name="exchangeStudent.school.id" items=schools?sort_by("name") empty="..." value=(exchangeStudent.school.id)!/]
    [@b.select label="培养层次" name="exchangeStudent.level.id" items=levels required="true" value=(exchangeStudent.level.id)! style=elementSTYLE/]
    [@b.textfield label="外校专业" name="exchangeStudent.majorName" value=(exchangeStudent.majorName)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.startend label="就读时间" name="exchangeStudent.beginOn,exchangeStudent.endOn" start=(exchangeStudent.beginOn)! end=(exchangeStudent.endOn)! required="true"/]
    [@b.formfoot]
      <input type="hidden" name="exchangeStudent.id" value="${(exchangeStudent.id)!}"/>
      <input type="hidden" name="exchangeStudent.category.id" value="${project.category.id}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
[#if !(exchangeStudent.id)?exists]
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
              "url": "${b.url("!loadStudent")}",
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
        init(document.exchangeStudentForm);
      });
    });
  </script>
  [/#if]
[@b.foot/]
