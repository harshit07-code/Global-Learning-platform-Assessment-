-- =========================================================================
-- SAMPLE SEED DATA
-- This file populates the database with basic records when the app starts.
-- =========================================================================

-- Seed Courses
INSERT INTO courses (title, description) VALUES
('Java Backend course', 'Learn the basics of Java: variables, loops, and simple backend Frameworks.'),
('Spring and SpringBoot', 'Learn Spring and SpringBoot frameworks of java that can help you to get placed'),
('Public Speaking and Confidence', 'A course designed to help kids speak confidently in front of crowds.');

-- Seed Parents
-- Notice we assign them different default timezones!
-- This will let us test timezone adjustments automatically.
INSERT INTO parents (name, timezone) VALUES
('Harshit Raghuvanshi', 'America/New_York'),    -- US Eastern Time (UTC-4 or UTC-5)
('Rohit Sharma', 'Asia/Kolkata'),        -- UK Time (UTC+0 or UTC+1)
('Virat Kohli', 'Europe/London');     -- Indian Standard Time (UTC+5:30)
