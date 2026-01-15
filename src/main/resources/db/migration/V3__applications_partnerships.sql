CREATE TABLE applications (
    id UUID PRIMARY KEY,
    opportunity_id UUID NOT NULL REFERENCES opportunities(id),
    applicant_org_id UUID NOT NULL REFERENCES organizations(id),
    status TEXT NOT NULL,
    proposal TEXT NOT NULL,
    price NUMERIC,
    timeline_days INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(opportunity_id, applicant_org_id)
);

CREATE INDEX idx_applications_opportunity_id ON applications(opportunity_id);
CREATE INDEX idx_applications_applicant_org_id ON applications(applicant_org_id);
CREATE INDEX idx_applications_status ON applications(status);

CREATE TABLE partnerships (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id),
    partner_org_id UUID NOT NULL REFERENCES organizations(id),
    type TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
