-- Tabela de termos de uso
CREATE TABLE termos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    versao VARCHAR(50) NOT NULL,
    tipo VARCHAR(100) NOT NULL, -- 'USO_IA', 'PRIVACIDADE', etc
    ativo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de aceite de termos por empresa
CREATE TABLE termos_aceite (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    termo_id UUID NOT NULL REFERENCES termos(id),
    empresa_id UUID NOT NULL,
    usuario_id UUID,
    ip_aceite VARCHAR(45),
    aceito_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(termo_id, empresa_id)
);

-- Índices
CREATE INDEX idx_termos_tipo_ativo ON termos(tipo, ativo);
CREATE INDEX idx_termos_aceite_empresa ON termos_aceite(empresa_id);
CREATE INDEX idx_termos_aceite_termo ON termos_aceite(termo_id);

-- Inserir termo padrão de uso de IA
INSERT INTO termos (titulo, conteudo, versao, tipo) VALUES (
    'Termos de Uso - Inteligência Artificial',
    'TERMOS DE USO - FUNCIONALIDADES DE INTELIGÊNCIA ARTIFICIAL

Ao utilizar as funcionalidades de Inteligência Artificial (IA) disponibilizadas pela plataforma DEV2B, você concorda com os seguintes termos:

1. FINALIDADE
As funcionalidades de IA têm como objetivo auxiliar profissionais de saúde na elaboração de resumos clínicos, análises de anamneses e sugestões de documentação. As sugestões geradas são baseadas em modelos de linguagem e devem ser sempre revisadas e validadas pelo profissional responsável.

2. RESPONSABILIDADE PROFISSIONAL
O conteúdo gerado pela IA é uma sugestão e não substitui o julgamento clínico do profissional de saúde. Toda informação gerada deve ser revisada, editada e validada antes de ser incorporada ao prontuário do paciente. O profissional é o único responsável pelas decisões clínicas e pelo conteúdo final registrado.

3. PRIVACIDADE E SEGURANÇA
- Os dados enviados para processamento pela IA são tratados com confidencialidade
- Não armazenamos permanentemente o conteúdo processado pela IA em servidores externos
- As informações são transmitidas de forma criptografada
- Não compartilhamos dados de pacientes com terceiros

4. LIMITAÇÕES
- A IA pode cometer erros ou gerar conteúdo impreciso
- Não deve ser utilizada como única fonte de informação para decisões clínicas
- Requer sempre supervisão e validação humana
- Pode não compreender contextos clínicos complexos ou específicos

5. CONSENTIMENTO
Ao aceitar estes termos, você declara:
- Ser um profissional de saúde devidamente habilitado
- Compreender as limitações da tecnologia de IA
- Comprometer-se a revisar e validar todo conteúdo gerado
- Utilizar a ferramenta de forma ética e responsável

6. MODIFICAÇÕES
Estes termos podem ser atualizados periodicamente. Você será notificado sobre alterações significativas e poderá revisar a nova versão antes de continuar utilizando as funcionalidades de IA.

Data de vigência: 11 de abril de 2026
Versão: 1.0',
    '1.0',
    'USO_IA'
);
