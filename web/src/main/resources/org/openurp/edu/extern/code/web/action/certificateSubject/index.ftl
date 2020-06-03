[#ftl]
[@b.head/]
<table class="indexpanel">
  <tr>
    <td class="index_view">
    [@b.form name="certificateSubjectSearchForm" action="!search" target="certificateSubjectlist" title="ui.searchForm" theme="search"]
      [@b.textfields names="certificateSubject.code;代码"/]
      [@b.textfields names="certificateSubject.name;名称"/]
      <input type="hidden" name="orderBy" value="certificateSubject.code"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="certificateSubjectlist" href="!search?orderBy=certificateSubject.code"/]
    </td>
  </tr>
</table>
[@b.foot/]
