[#ftl]
[@b.head/]
[@b.grid items=certificateSubjects var="certificateSubject"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="8%" property="code" title="代码"]${certificateSubject.code}[/@]
    [@b.col width="30%" property="name" title="名称"/]
    [@b.col width="7%" property="category.name" title="证书类型"/]
    [@b.col width="30%" property="institutionName" title="发证机构"/]
    [@b.col width="20%" property="beginOn" title="生效日期"]${certificateSubject.beginOn!}~${certificateSubject.endOn!}[/@]
  [/@]
[/@]
[@b.foot/]
