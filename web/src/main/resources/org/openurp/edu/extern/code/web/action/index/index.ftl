[#ftl]
[@b.head/]
[#assign codes={}]
[#assign codes=codes+{'证书考试科目':'/code/certificate-subject'}]
[#assign codes=codes+{'证书类型':'/code/certificate-category'}]
[#assign codes=codes+{'外校信息':'/code/school'}]

[@b.nav class="nav nav-tabs nav-tabs-compact"  id="code_nav"]
  [#list codes?keys as code]
  [@b.navitem href=codes[code]  active=(code_index=0) target="codelist"]${code}[/@]
  [/#list]
[/@]
[@b.div id="codelist" href="/code/certificate-subject"/]

[@b.foot/]
