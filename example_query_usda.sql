USE `nutrition`;

SELECT FOOD_DES.NDB_No, FOOD_DES.Long_Desc, NUT_DATA.NDB_No, NUT_DATA.Nutr_No, NUT_DATA.Nutr_Val, NUTR_DEF.NutrDesc
	FROM FOOD_DES, NUT_DATA, NUTR_DEF
    WHERE FOOD_DES.Long_Desc LIKE "%smoothie%"
    AND FOOD_DES.NDB_No = NUT_DATA.NDB_No 
    AND NUT_DATA.Nutr_No = NUTR_DEF.Nutr_No
    
    
    
