[#ftl]
[@b.head/]
[@b.toolbar title="修改出版社"]bar.addBack();[/@]
[@b.tabs]
  [@b.form action=b.rest.save(certificateCategory) theme="list"]
    [@b.textfield name="certificateCategory.code" label="代码" value="${certificateCategory.code!}" required="true" maxlength="20"/]
    [@b.textfield name="certificateCategory.name" label="名称" value="${certificateCategory.name!}" required="true" maxlength="20"/]
    [@b.textfield name="certificateCategory.enName" label="英文名" value="${certificateCategory.enName!}" maxlength="100"/]
    [@b.startend label="有效期"
      name="certificateCategory.beginOn,certificateCategory.endOn" required="true,false"
      start=certificateCategory.beginOn end=certificateCategory.endOn format="date"/]
    [@b.textfield name="certificateCategory.remark" label="备注" value="${certificateCategory.remark!}" maxlength="3"/]
    [@b.formfoot]
      [@b.reset/]&nbsp;&nbsp;[@b.submit value="action.submit"/]
    [/@]
  [/@]
[/@]
[@b.foot/]
