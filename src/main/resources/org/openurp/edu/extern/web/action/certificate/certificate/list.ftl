[#ftl]
[@b.head/]
[@b.grid items=certificates var="certificate"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="8%" property="code" title="代码"]${certificate.code}[/@]
    [@b.col property="name" title="名称"/]
    [@b.col width="20%" property="institutionName" title="发证机构"/]
    [@b.col width="7%" property="category.name" title="证书类型"/]
    [@b.col width="20%" property="beginOn" title="生效日期"]${certificate.beginOn!}~${certificate.endOn!}[/@]
  [/@]
[/@]
[@b.foot/]
