ALTER TABLE agendamentos
    ADD COLUMN IF NOT EXISTS whereby_meeting_id  VARCHAR(255),
    ADD COLUMN IF NOT EXISTS whereby_host_url    TEXT,
    ADD COLUMN IF NOT EXISTS whereby_viewer_url  TEXT;
