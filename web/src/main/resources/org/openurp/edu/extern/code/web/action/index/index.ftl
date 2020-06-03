[#ftl]
[@b.head/]
[#assign codes={}]
[#assign codes=codes+{'证书考试科目':'/code/certificate-subject'}]
[#assign codes=codes+{'证书类型':'/code/certificate-category'}]
[#assign codes=codes+{'外校信息':'/code/school'}]

[@b.nav class="nav nav-tabs nav-tabs-compact"  id="code_nav"]
  [#list codes?keys as code]
  [#if code_index<9]
  <li role="presentation" [#if code_index=0]class="active"[/#if]>[@b.a href=codes[code] target="codelist"]${code}[/@]</li>
  [/#if]
  [/#list]
[/@]
[@b.div id="codelist" href="/code/certificate-subject"/]
<script>
  jQuery(document).ready(function(){
    jQuery('#code_nav>li').bind("click",function(e){
      jQuery("#code_nav>li").removeClass("active");
      jQuery(this).addClass("active");
    });
  });
</script>

[@b.foot/]
