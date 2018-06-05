insert into tariffs values(0, 10, 2, 8) on conflict do nothing;
insert into configurations values(0, 'capacity', 20) on conflict do nothing;