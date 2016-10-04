SELECT
  AUTHOR,
  TITLE
FROM BOOKS
  INNER JOIN AUTHORS ON AUTHORS.SHORTNAME = BOOKS.AUTHOR
WHERE AUTHORS.LASTNAME LIKE ?;
