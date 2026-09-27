-- ==============================================================================
-- INITIAL DEMONSTRATION & SAMPLE DATA FOR SUPABASE
-- Only executed if institutions table is completely empty!
-- ==============================================================================

-- 1. Insert Sample Institutions across all 4 categories
INSERT INTO institutions (id, name, category, physical_address, registration_date, status) VALUES
(1, 'Kilimani Primary School', 'PRIMARY_SCHOOL', 'Argwings Kodhek Rd, Nairobi', '2026-08-01', 'ACTIVE'),
(2, 'St. Jude Junior Academy', 'JUNIOR_SCHOOL', 'Kenyatta Avenue, Nakuru', '2026-08-10', 'DEFAULTER'),
(3, 'Highlands Senior High School', 'SENIOR_SCHOOL', 'Nyeri Road, Nyeri', '2026-08-15', 'DISCONNECTED'),
(4, 'Great Rift Valley College', 'COLLEGE', 'Eldoret Town Center', '2026-08-20', 'ACTIVE');

-- 2. Insert Contact Persons
INSERT INTO contact_persons (institution_id, full_name, designation, phone, email, national_id) VALUES
(1, 'Mary Wambui', 'Head Teacher', '+254712345678', 'headteacher@kilimanipri.ac.ke', 'ID-21849102'),
(2, 'David Kiprono', 'ICT Director', '+254723456789', 'ict@stjudejunior.ac.ke', 'ID-30918274'),
(3, 'Grace Njeri', 'Principal', '+254734567890', 'principal@highlandssenior.ac.ke', 'ID-19827364'),
(4, 'Dr. Samuel Ochieng', 'Dean of Students', '+254745678901', 'dean@greatriftcollege.ac.ke', 'ID-18274659');

-- 3. Insert Infrastructure Requirements
INSERT INTO infrastructure_requirements (institution_id, num_users, is_ready, pcs_purchased, lan_nodes, pc_cost, lan_cost, base_installation_fee, total_installation_cost) VALUES
(1, 150, TRUE, 0, 0, 0.00, 0.00, 10000.00, 10000.00),
(2, 320, FALSE, 5, 8, 200000.00, 10000.00, 10000.00, 220000.00),
(3, 750, FALSE, 12, 25, 480000.00, 30000.00, 10000.00, 520000.00),
(4, 1800, FALSE, 20, 50, 800000.00, 40000.00, 10000.00, 850000.00);

-- 4. Subscriptions
INSERT INTO subscriptions (institution_id, bandwidth_mbps, base_monthly_cost, is_upgraded, discount_percent, final_monthly_cost, start_date, active) VALUES
(1, 10, 2000.00, FALSE, 0.00, 2000.00, '2026-08-05', TRUE),
(2, 20, 3500.00, FALSE, 0.00, 3500.00, '2026-08-12', TRUE),
(3, 50, 7000.00, TRUE, 10.00, 6300.00, '2026-08-18', TRUE),
(4, 25, 4000.00, TRUE, 10.00, 3600.00, '2026-08-25', TRUE);

-- 5. Captured Payments
INSERT INTO payments (institution_id, payment_type, amount, reference_no, notes) VALUES
(1, 'REGISTRATION', 8500.00, 'REG-2026-001', 'Registration fee for Kilimani Primary'),
(2, 'REGISTRATION', 8500.00, 'REG-2026-002', 'Registration fee for St. Jude Junior'),
(3, 'REGISTRATION', 8500.00, 'REG-2026-003', 'Registration fee for Highlands Senior'),
(4, 'REGISTRATION', 8500.00, 'REG-2026-004', 'Registration fee for Great Rift Valley College'),
(1, 'INSTALLATION', 10000.00, 'INST-2026-001', 'Ready installation fee'),
(2, 'INSTALLATION', 220000.00, 'INST-2026-002', 'Installation with 5 PCs and 8 nodes'),
(3, 'INSTALLATION', 520000.00, 'INST-2026-003', 'Installation with 12 PCs and 25 nodes'),
(4, 'INSTALLATION', 850000.00, 'INST-2026-004', 'Installation with 20 PCs and 50 nodes');

-- 6. Monthly Bills & Status
INSERT INTO bills (institution_id, billing_month, billing_year, base_charge, overdue_fine, reconnection_fee, total_due, amount_paid, due_date, disconnection_date, status) VALUES
(1, 8, 2026, 2000.00, 0.00, 0.00, 2000.00, 2000.00, '2026-08-31', '2026-09-10', 'PAID'),
(2, 8, 2026, 3500.00, 525.00, 0.00, 4025.00, 0.00, '2026-08-31', '2026-09-10', 'OVERDUE'),
(3, 8, 2026, 6300.00, 945.00, 0.00, 7245.00, 0.00, '2026-08-31', '2026-09-10', 'DISCONNECTED'),
(4, 8, 2026, 3600.00, 540.00, 1000.00, 5140.00, 5140.00, '2026-08-31', '2026-09-10', 'PAID');

INSERT INTO payments (institution_id, payment_type, amount, reference_no, notes) VALUES
(1, 'MONTHLY', 2000.00, 'MTH-2026-001', 'August 2026 monthly payment paid on time'),
(4, 'MONTHLY', 4140.00, 'MTH-2026-004', 'August bill and overdue fine paid'),
(4, 'RECONNECTION', 1000.00, 'REC-2026-001', 'Reconnection surcharge fee paid');

-- Reset sequences to highest id
SELECT setval('institutions_id_seq', COALESCE((SELECT MAX(id) FROM institutions), 1));
SELECT setval('contact_persons_id_seq', COALESCE((SELECT MAX(id) FROM contact_persons), 1));
SELECT setval('infrastructure_requirements_id_seq', COALESCE((SELECT MAX(id) FROM infrastructure_requirements), 1));
SELECT setval('subscriptions_id_seq', COALESCE((SELECT MAX(id) FROM subscriptions), 1));
SELECT setval('bills_id_seq', COALESCE((SELECT MAX(id) FROM bills), 1));
SELECT setval('payments_id_seq', COALESCE((SELECT MAX(id) FROM payments), 1));
