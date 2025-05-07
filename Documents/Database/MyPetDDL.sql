CREATE TABLE IF NOT EXISTS `mypet`.`pets` (
  `petid` INT NOT NULL AUTO_INCREMENT,
  `petname` VARCHAR(255) NOT NULL,
  `petowner` VARCHAR(255) NOT NULL,
  `pettype` VARCHAR(255) NOT NULL,
  `petaffectionlevel` INT NOT NULL DEFAULT '50',
  `pethungerlevel` INT NOT NULL DEFAULT '50',
  `petxlocation` INT NOT NULL DEFAULT '200',
  `petylocation` INT NOT NULL DEFAULT '390',
  `petdirection` VARCHAR(255) NOT NULL DEFAULT 'right',
  `petaction` VARCHAR(255) NULL DEFAULT NULL,
  `shared` INT NULL DEFAULT NULL,
  PRIMARY KEY (`petid`),
  UNIQUE INDEX `petId_UNIQUE` (`petid` ASC) VISIBLE,
  INDEX `FKPetType_idx` (`pettype` ASC) VISIBLE,
  INDEX `FKOwner` (`petowner` ASC) VISIBLE,
  CONSTRAINT `FKOwner`
    FOREIGN KEY (`petowner`)
    REFERENCES `mypet`.`owners` (`username`)
    ON DELETE CASCADE,
  CONSTRAINT `FKPetType`
    FOREIGN KEY (`pettype`)
    REFERENCES `mypet`.`pettypes` (`name`))
ENGINE = InnoDB
AUTO_INCREMENT = 25
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `mypet`.`pettypes` (
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`name`),
  UNIQUE INDEX `PetTypeName_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `mypet`.`owners` (
  `username` VARCHAR(255) NOT NULL,
  `displayname` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`username`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `mypet`.`food` (
  `foodid` INT NOT NULL AUTO_INCREMENT,
  `petid` INT NOT NULL,
  `foodtype` VARCHAR(255) NOT NULL,
  `foodxlocation` INT NULL DEFAULT NULL,
  `foodylocation` INT NULL DEFAULT NULL,
  `createdtimestamp` DATETIME NOT NULL,
  PRIMARY KEY (`foodid`),
  INDEX `FKFoodName_idx` (`foodtype` ASC) VISIBLE,
  INDEX `FKPetId_idx` (`petid` ASC) VISIBLE,
  CONSTRAINT `FKFoodName`
    FOREIGN KEY (`foodtype`)
    REFERENCES `mypet`.`foodtypes` (`name`)
    ON UPDATE CASCADE,
  CONSTRAINT `FKPetId`
    FOREIGN KEY (`petid`)
    REFERENCES `mypet`.`pets` (`petid`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 467
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `mypet`.`foodtypes` (
  `name` VARCHAR(255) NOT NULL,
  `satiatehunger` INT NOT NULL DEFAULT '0',
  `satiateaffection` INT NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;