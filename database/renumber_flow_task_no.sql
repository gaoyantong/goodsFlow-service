SET @flow_task_row_no := 0;

UPDATE `gf_flow_task` task
JOIN (
  SELECT ordered_task.`id`, (@flow_task_row_no := @flow_task_row_no + 1) AS row_no
  FROM (
    SELECT `id`
    FROM `gf_flow_task`
    ORDER BY COALESCE(`createdAt`, `generatedAt`, `updatedAt`, `id`), `id`
  ) ordered_task
) numbered_task ON numbered_task.`id` = task.`id`
SET task.`taskNo` = CONCAT('TMP_FLOW_', LPAD(numbered_task.row_no, 6, '0'));

UPDATE `gf_flow_task`
SET `taskNo` = CONCAT('FL', SUBSTRING(`taskNo`, 10))
WHERE `taskNo` LIKE 'TMP_FLOW_%';
