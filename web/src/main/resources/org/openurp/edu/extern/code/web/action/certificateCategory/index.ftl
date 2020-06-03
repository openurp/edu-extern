[#ftl]
[@b.head/]
<table class="indexpanel">
  <tr>
    <td class="index_view">
    [@b.form name="certificateCategorySearchForm" action="!search" target="certificateCategorylist" title="ui.searchForm" theme="search"]
      [@b.textfields names="certificateCategory.code;代码"/]
      [@b.textfields names="certificateCategory.name;名称"/]
      <input type="hidden" name="orderBy" value="certificateCategory.code"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="certificateCategorylist" href="!search?orderBy=certificateCategory.code"/]
    </td>
  </tr>
</table>
[@b.foot/]
