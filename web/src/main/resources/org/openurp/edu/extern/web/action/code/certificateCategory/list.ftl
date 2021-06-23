[#ftl]
[@b.head/]
[@b.grid items=certificateCategories var="certificateCategory"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="15%" property="code" title="代码"]${certificateCategory.code}[/@]
    [@b.col width="45%" property="name" title="名称"/]
    [@b.col width="15%" property="enName" title="英文名"]${certificateCategory.enName!}[/@]
    [@b.col width="10%" property="beginOn" title="生效日期"]${certificateCategory.beginOn!}[/@]
    [@b.col width="10%" property="endOn" title="失效日期"]${certificateCategory.endOn!}[/@]
  [/@]
[/@]
[@b.foot/]
