-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u2
-- http://www.phpmyadmin.net
--
-- Machine: localhost
-- Gegenereerd op: 26 jan 2017 om 20:34
-- Serverversie: 5.5.52-0+deb8u1
-- PHP-versie: 5.6.28-0+deb8u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Databank: `PinIt`
--

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `DEPARTMENT`
--

CREATE TABLE IF NOT EXISTS `DEPARTMENT` (
  `DepartmentID` int(6) NOT NULL,
  `DepartmentName` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `DESIGN`
--

CREATE TABLE IF NOT EXISTS `DESIGN` (
  `DesignID` int(6) NOT NULL,
  `DepartmentID` int(6) NOT NULL,
  `RSSLink` varchar(1000) NOT NULL,
  `RSSTitle` varchar(200) NOT NULL,
  `Colour` varchar(7) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `POST`
--

CREATE TABLE IF NOT EXISTS `POST` (
`PostID` int(6) NOT NULL,
  `UserID` int(6) NOT NULL,
  `PostType` varchar(20) NOT NULL,
  `Title` varchar(20) NOT NULL,
  `DateTime` datetime DEFAULT NULL,
  `DateTimeVisible` datetime DEFAULT NULL,
  `Message` varchar(100) NOT NULL,
  `AgendaTime` date DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8;



-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `USER`
--

CREATE TABLE IF NOT EXISTS `USER` (
  `UserID` int(6) NOT NULL,
  `UserTypeID` int(6) NOT NULL,
  `AndroidID` varchar(200) DEFAULT NULL,
  `FirstName` varchar(50) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `ImagePath` varchar(45) DEFAULT NULL,
  `Presence` tinyint(1) NOT NULL DEFAULT '0',
  `Active` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `USERHASDEPARTMENT`
--

CREATE TABLE IF NOT EXISTS `USERHASDEPARTMENT` (
  `DepartmentID` int(6) NOT NULL,
  `UserID` int(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `USERTYPE`
--

CREATE TABLE IF NOT EXISTS `USERTYPE` (
  `UserTypeID` int(6) NOT NULL,
  `UserType` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




--
-- Indexen voor geëxporteerde tabellen
--

--
-- Indexen voor tabel `DEPARTMENT`
--
ALTER TABLE `DEPARTMENT`
 ADD PRIMARY KEY (`DepartmentID`);

--
-- Indexen voor tabel `DESIGN`
--
ALTER TABLE `DESIGN`
 ADD PRIMARY KEY (`DesignID`), ADD KEY `fk_DESIGN_DEPARTMENT` (`DepartmentID`);

--
-- Indexen voor tabel `POST`
--
ALTER TABLE `POST`
 ADD PRIMARY KEY (`PostID`), ADD KEY `fk_Post_USER1` (`UserID`);

--
-- Indexen voor tabel `USER`
--
ALTER TABLE `USER`
 ADD PRIMARY KEY (`UserID`), ADD KEY `fk_USER_table11` (`UserTypeID`);

--
-- Indexen voor tabel `USERHASDEPARTMENT`
--
ALTER TABLE `USERHASDEPARTMENT`
 ADD KEY `fk_USERHASDEPARTMENT_DEPARTMENT1` (`DepartmentID`), ADD KEY `fk_USERTOUHD_USER1` (`UserID`);

--
-- Indexen voor tabel `USERTYPE`
--
ALTER TABLE `USERTYPE`
 ADD PRIMARY KEY (`UserTypeID`);

--
-- AUTO_INCREMENT voor geëxporteerde tabellen
--

--
-- AUTO_INCREMENT voor een tabel `POST`
--
ALTER TABLE `POST`
MODIFY `PostID` int(6) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=187;
--
-- Beperkingen voor geëxporteerde tabellen
--

--
-- Beperkingen voor tabel `DESIGN`
--
ALTER TABLE `DESIGN`
ADD CONSTRAINT `fk_DESIGN_DEPARTMENT` FOREIGN KEY (`DepartmentID`) REFERENCES `DEPARTMENT` (`DepartmentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Beperkingen voor tabel `POST`
--
ALTER TABLE `POST`
ADD CONSTRAINT `fk_Post_USER1` FOREIGN KEY (`UserID`) REFERENCES `USER` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Beperkingen voor tabel `USER`
--
ALTER TABLE `USER`
ADD CONSTRAINT `fk_USER_table11` FOREIGN KEY (`UserTypeID`) REFERENCES `USERTYPE` (`UserTypeID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Beperkingen voor tabel `USERHASDEPARTMENT`
--
ALTER TABLE `USERHASDEPARTMENT`
ADD CONSTRAINT `fk_USERHASDEPARTMENT_DEPARTMENT1` FOREIGN KEY (`DepartmentID`) REFERENCES `DEPARTMENT` (`DepartmentID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `fk_USERTOUHD_USER1` FOREIGN KEY (`UserID`) REFERENCES `USER` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
