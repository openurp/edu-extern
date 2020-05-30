[#ftl]
[@b.head/]
[@b.toolbar title="冲抵学分上限"/]
   <table width="100%"  class="indexpanel" height="89%">
    <tr>
     <td valign="top"  style="width:180px" class="index_view">
      [@b.form name="searchForm" action="!search" title="ui.searchForm" target="listFrame" theme="search"]
     [#include "searchForm.ftl"/]
     [/@]
     </td>
     <td valign="top">
     [@b.div id="listFrame"/]
     </td>
    </tr>
  <table>
 <script>
  var form = document.searchForm;
  function search(pageNo,pageSize,orderBy){
    form.target="listFrame";
    form.action="${b.url('!search')}";
    bg.form.submit(form)
  }
  search();
 </script>
[@b.foot/]
