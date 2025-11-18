-- RMS Service Database Schema
-- PostgreSQL 15+

-- ============================================
-- Organizations (기관)
-- ============================================
CREATE TABLE IF NOT EXISTS organizations (
    id BIGSERIAL PRIMARY KEY,
    serial VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    registration_number VARCHAR(100),
    nursing_number VARCHAR(100),
    branch_code VARCHAR(50),
    branch_name VARCHAR(200),
    employee_id VARCHAR(100),
    employee_name VARCHAR(100),
    employee_phone VARCHAR(20),
    type VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100)
);

CREATE INDEX idx_organizations_serial ON organizations(serial);
CREATE INDEX idx_organizations_name ON organizations(name);
CREATE INDEX idx_organizations_created_at ON organizations(created_at);

COMMENT ON TABLE organizations IS '기관 정보';
COMMENT ON COLUMN organizations.serial IS '기관 일련번호';
COMMENT ON COLUMN organizations.name IS '기관명';
COMMENT ON COLUMN organizations.registration_number IS '사업자등록번호';
COMMENT ON COLUMN organizations.nursing_number IS '요양기관번호';

-- ============================================
-- Patients (환자)
-- ============================================
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    serial VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    sex CHAR(1) CHECK (sex IN ('M', 'F')),
    age INTEGER CHECK (age > 0),
    birth DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100)
);

CREATE INDEX idx_patients_serial ON patients(serial);
CREATE INDEX idx_patients_name ON patients(name);
CREATE INDEX idx_patients_created_at ON patients(created_at);

COMMENT ON TABLE patients IS '환자 정보';
COMMENT ON COLUMN patients.serial IS '환자 일련번호';
COMMENT ON COLUMN patients.sex IS '성별 (M: 남성, F: 여성)';

-- ============================================
-- Sample Types (샘플 유형)
-- ============================================
CREATE TABLE IF NOT EXISTS sample_types (
    id BIGSERIAL PRIMARY KEY,
    serial VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100)
);

CREATE INDEX idx_sample_types_serial ON sample_types(serial);
CREATE INDEX idx_sample_types_name ON sample_types(name);

COMMENT ON TABLE sample_types IS '샘플 유형';
COMMENT ON COLUMN sample_types.serial IS '샘플 유형 일련번호';

-- ============================================
-- Samples (샘플)
-- ============================================
CREATE TABLE IF NOT EXISTS samples (
    id BIGSERIAL PRIMARY KEY,
    count INTEGER NOT NULL CHECK (count > 0),
    age INTEGER CHECK (age >= 0),
    sampling_on DATE NOT NULL,
    sample_type_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT fk_samples_sample_type FOREIGN KEY (sample_type_id) REFERENCES sample_types(id)
);

CREATE INDEX idx_samples_sample_type_id ON samples(sample_type_id);
CREATE INDEX idx_samples_sampling_on ON samples(sampling_on);

COMMENT ON TABLE samples IS '샘플 정보';
COMMENT ON COLUMN samples.count IS '샘플 수량';
COMMENT ON COLUMN samples.sampling_on IS '채취일';

-- ============================================
-- Services (서비스)
-- ============================================
CREATE TABLE IF NOT EXISTS services (
    id BIGSERIAL PRIMARY KEY,
    serial VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price BIGINT CHECK (price >= 0),
    category_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100)
);

CREATE INDEX idx_services_serial ON services(serial);
CREATE INDEX idx_services_name ON services(name);
CREATE INDEX idx_services_category_id ON services(category_id);

COMMENT ON TABLE services IS '검사 서비스';
COMMENT ON COLUMN services.serial IS '서비스 일련번호';

-- ============================================
-- Requests (의뢰)
-- ============================================
CREATE TABLE IF NOT EXISTS requests (
    id BIGSERIAL PRIMARY KEY,
    serial VARCHAR(100) NOT NULL UNIQUE,
    request_date_from DATE NOT NULL,
    request_date_to DATE NOT NULL,
    department VARCHAR(100),
    ward VARCHAR(100),
    physician VARCHAR(100),
    memo TEXT,
    genome_price BIGINT,
    labs_price BIGINT,
    organization_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    sample_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT fk_requests_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_requests_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_requests_sample FOREIGN KEY (sample_id) REFERENCES samples(id),
    CONSTRAINT chk_requests_date_range CHECK (request_date_from <= request_date_to)
);

CREATE INDEX idx_requests_serial ON requests(serial);
CREATE INDEX idx_requests_organization_id ON requests(organization_id);
CREATE INDEX idx_requests_patient_id ON requests(patient_id);
CREATE INDEX idx_requests_sample_id ON requests(sample_id);
CREATE INDEX idx_requests_date_range ON requests(request_date_from, request_date_to);
CREATE INDEX idx_requests_created_at ON requests(created_at);

COMMENT ON TABLE requests IS '검사 의뢰';
COMMENT ON COLUMN requests.serial IS '의뢰 일련번호';
COMMENT ON COLUMN requests.request_date_from IS '의뢰 시작일';
COMMENT ON COLUMN requests.request_date_to IS '의뢰 종료일';

-- ============================================
-- Extensions (추가 정보 정의)
-- ============================================
CREATE TABLE IF NOT EXISTS extensions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    data_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT chk_extensions_data_type CHECK (data_type IN ('STRING', 'INTEGER', 'DECIMAL', 'DATE', 'DATETIME', 'BOOLEAN'))
);

CREATE INDEX idx_extensions_code ON extensions(code);
CREATE INDEX idx_extensions_is_required ON extensions(is_required);

COMMENT ON TABLE extensions IS '추가 정보 정의';
COMMENT ON COLUMN extensions.code IS '추가 정보 코드';
COMMENT ON COLUMN extensions.data_type IS '데이터 타입';
COMMENT ON COLUMN extensions.is_required IS '필수 여부';

-- ============================================
-- Request Extensions (의뢰 추가 정보)
-- ============================================
CREATE TABLE IF NOT EXISTS request_extensions (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    extension_code VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_request_extensions_request FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_request_extensions_extension FOREIGN KEY (extension_code) REFERENCES extensions(code),
    CONSTRAINT uk_request_extensions UNIQUE (request_id, extension_code)
);

CREATE INDEX idx_request_extensions_request_id ON request_extensions(request_id);
CREATE INDEX idx_request_extensions_extension_code ON request_extensions(extension_code);

COMMENT ON TABLE request_extensions IS '의뢰 추가 정보 값';
COMMENT ON COLUMN request_extensions.request_id IS '의뢰 ID';
COMMENT ON COLUMN request_extensions.extension_code IS '추가 정보 코드';
COMMENT ON COLUMN request_extensions.value IS '값';

-- ============================================
-- Sample Data (개발용)
-- ============================================

-- Sample Types
INSERT INTO sample_types (serial, name, description, created_by) VALUES
('ST001', 'Blood', '혈액 샘플', 'system'),
('ST002', 'Tissue', '조직 샘플', 'system'),
('ST003', 'Saliva', '타액 샘플', 'system')
ON CONFLICT (serial) DO NOTHING;

-- Extensions
INSERT INTO extensions (code, name, description, data_type, is_required, created_by) VALUES
('EXT001', '긴급여부', '긴급 검사 여부', 'BOOLEAN', false, 'system'),
('EXT002', '검사목적', '검사 목적 상세', 'STRING', false, 'system'),
('EXT003', '특이사항', '특이사항 메모', 'STRING', false, 'system')
ON CONFLICT (code) DO NOTHING;

-- Organizations (테스트용)
INSERT INTO organizations (serial, name, type, created_by) VALUES
('ORG001', '서울대학교병원', 'HOSPITAL', 'system'),
('ORG002', '삼성서울병원', 'HOSPITAL', 'system'),
('ORG003', '아산병원', 'HOSPITAL', 'system')
ON CONFLICT (serial) DO NOTHING;
