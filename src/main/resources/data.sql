insert into data.tariffs values(0, 10, 2, 8) on conflict do nothing;
insert into data.configurations values(0, 'capacity', 20) on conflict do nothing;