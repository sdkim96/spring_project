SET @givenLatitude = 37.566535;
SET @givenLongitude = 126.9779692;
SET @radius = 0.0045; -- 대략 500m를 위도로 환산한 값

--- O(logN)
-- 위도 기준으로 가까운 카페 검색
(SELECT *, 
       (6371 * acos(cos(radians(@givenLatitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(@givenLongitude)) + sin(radians(@givenLatitude)) * sin(radians(latitude)))) AS distance
 FROM cafe_table
 WHERE latitude BETWEEN @givenLatitude - @radius AND @givenLatitude + @radius
 ORDER BY distance ASC
 LIMIT 100) 
UNION
-- 경도 기준으로 가까운 카페 검색
(SELECT *, 
       (6371 * acos(cos(radians(@givenLatitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(@givenLongitude)) + sin(radians(@givenLatitude)) * sin(radians(latitude)))) AS distance
 FROM cafe_table
 WHERE longitude BETWEEN @givenLongitude - @radius AND @givenLongitude + @radius
 ORDER BY distance ASC
 LIMIT 100)
ORDER BY distance ASC
LIMIT 50;



-----------------------------------------------------------------------
--- O(N)
SET @givenLatitude = 37.566535;
SET @givenLongitude = 126.9779692;
SET @radius = 6371; -- 지구의 반지름 (킬로미터 단위)

SELECT
  *,
  (
    @radius * acos(
      cos(radians(@givenLatitude)) * cos(radians(latitude)) *
      cos(radians(longitude) - radians(@givenLongitude)) +
      sin(radians(@givenLatitude)) * sin(radians(latitude))
    )
  ) AS distance
FROM
  cafe_table
HAVING
  distance < 5 -- 5km 이내
ORDER BY
  distance
LIMIT 50;
