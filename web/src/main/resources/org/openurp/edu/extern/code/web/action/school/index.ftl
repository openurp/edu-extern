[#ftl]
[@b.head/]
<table class="indexpanel">
  <tr>
    <td class="index_view">
    [@b.form name="exchangeSchoolSearchForm" action="!search" target="exchangeSchoollist" title="ui.searchForm" theme="search"]
      [@b.textfields names="exchangeSchool.code;代码"/]
      [@b.textfields names="exchangeSchool.name;名称"/]
      <input type="hidden" name="orderBy" value="exchangeSchool.code"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="exchangeSchoollist" href="!search?orderBy=exchangeSchool.code"/]
    </td>
  </tr>
</table>
[@b.foot/]
