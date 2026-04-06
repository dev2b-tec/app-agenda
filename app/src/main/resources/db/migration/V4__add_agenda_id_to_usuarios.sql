ALTER TABLE usuarios ADD COLUMN agenda_id UUID REFERENCES agendas(id);
