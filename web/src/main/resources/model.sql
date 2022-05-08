create table edu.cert_exam_signup_configs (semester_id integer not null, name varchar(255) not null, project_id integer not null, begin_at timestamp not null, prediction boolean not null, id bigint not null, notice varchar(255) not null, category_id integer not null, end_at timestamp not null, opened boolean not null, code varchar(255) not null);
create table edu.cert_exam_signup_settings (exam_begin_at smallint not null, subject_id integer not null, re_exam_allowed boolean not null, config_id bigint not null, fee_of_material integer not null, depends_on_id integer, max_std integer not null, exam_end_at smallint not null, fee_of_outline integer not null, id bigint not null, fee_of_signup integer not null, exam_on date);
create table edu.cert_exam_signup_settings_exclusives (cert_exam_signup_setting_id bigint not null, certificate_subject_id integer not null);
create table edu.cert_exam_signups (semester_id integer not null, subject_id integer not null, std_id bigint not null, exam_no varchar(255), id bigint not null, fee integer not null, ip varchar(255) not null, updated_at timestamp not null);
create table edu.cert_exam_std_scopes (setting_id bigint not null, grades varchar(255), include_in boolean not null, level_id integer not null, id bigint not null);
create table edu.cert_exam_std_scopes_codes (cert_exam_std_scope_id bigint not null, value_ varchar(255) not null);

alter table edu.cert_exam_signup_configs add constraint pk_2vkqecejwk4o50thkk6n9e7ym primary key (id);
alter table edu.cert_exam_signup_settings add constraint pk_c1qecrbx35hp1vf7kj84cq6tc primary key (id);
alter table edu.cert_exam_signup_settings_exclusives add constraint pk_t7y7g9nrfpjr92couq71ug2tt primary key (cert_exam_signup_setting_id,certificate_subject_id);
alter table edu.cert_exam_signups add constraint pk_2m8insqmyamm3kfbthuwdfc5b primary key (id);
alter table edu.cert_exam_std_scopes add constraint pk_o5bulxfep5792dry8cb6jbfy2 primary key (id);
alter table edu.cert_exam_std_scopes_codes add constraint pk_lkm6jdsa6jk3m1uyiqhn8vjg2 primary key (cert_exam_std_scope_id,value_);

create index idx_keo2didkmufqujrl995kr7953 on edu.cert_exam_signup_settings (config_id);
create index idx_rifk2aon5h5y3xwxr4d6orqvp on edu.cert_exam_signup_settings_exclusives (cert_exam_signup_setting_id);
create index idx_8s0gtf8vnxiuvy0bat0v0hfxi on edu.cert_exam_std_scopes (setting_id);
create index idx_5ku4ljq6o5twu61pxuve7c4yc on edu.cert_exam_std_scopes_codes (cert_exam_std_scope_id);