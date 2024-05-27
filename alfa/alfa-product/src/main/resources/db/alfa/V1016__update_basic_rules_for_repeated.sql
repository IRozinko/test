update
  settings.property
set
  text_value = '{ "newClientCheck": ' || text_value || ',' || '"repeatedClientCheck": ' || text_value || '}'
where
  "name" in(
    'LendingRulesBasic'
  )
