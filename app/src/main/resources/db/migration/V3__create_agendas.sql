CREATE TABLE agendas (
    id                              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    controle_comissoes              BOOLEAN DEFAULT false,
    recurso_desativado_comissoes    BOOLEAN DEFAULT false,
    overbooking_profissionais       BOOLEAN DEFAULT false,
    recurso_desativado_overbooking  BOOLEAN DEFAULT false,
    fila_espera                     BOOLEAN DEFAULT false,
    recurso_desativado_fila         BOOLEAN DEFAULT false,
    bloquear_edicao_evolucao        BOOLEAN DEFAULT false,
    recurso_desativado_evolucao     BOOLEAN DEFAULT false,
    
    -- Horário de Funcionamento
    seg_abertura                    TIME,
    seg_fechamento                  TIME,
    seg_aberto                      BOOLEAN DEFAULT true,
    
    ter_abertura                    TIME,
    ter_fechamento                  TIME,
    ter_aberto                      BOOLEAN DEFAULT true,
    
    qua_abertura                    TIME,
    qua_fechamento                  TIME,
    qua_aberto                      BOOLEAN DEFAULT true,
    
    qui_abertura                    TIME,
    qui_fechamento                  TIME,
    qui_aberto                      BOOLEAN DEFAULT true,
    
    sex_abertura                    TIME,
    sex_fechamento                  TIME,
    sex_aberto                      BOOLEAN DEFAULT true,
    
    sab_abertura                    TIME,
    sab_fechamento                  TIME,
    sab_aberto                      BOOLEAN DEFAULT false,
    
    dom_abertura                    TIME,
    dom_fechamento                  TIME,
    dom_aberto                      BOOLEAN DEFAULT false,
    
    -- Horário de Almoço
    almoco_inicio                   TIME,
    almoco_fim                      TIME,
    ativar_horario_almoco           BOOLEAN DEFAULT false,
    
    created_at                      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMP
);
