'use client';

import style from "@/app/_component/footer.module.css";
import React, {useState} from "react";
import Image from "next/image";
import footerImg from "@/../public/footer-img.png"

export default function Footer() {
    const [hoveredIcon, setHoveredIcon] = useState<string | null>(null);

    const handleMouseEnter = (iconName: string) => {
        setHoveredIcon(iconName);
    };

    const handleMouseLeave = () => {
        setHoveredIcon(null);
    };

    const getIconSrc = (iconName: string) => {
        if (hoveredIcon === iconName) {
            return `/avatar/${iconName}.png`;
        }
        return `/avatar/${iconName}-1.png`;
    };

    return (
        <footer className={style.footer}>
            <div className={style.menu}>
                <div className={style.iconContainer}>
                    <label className={style.followUsOn}>Follow Us On</label>
                    <div className={style.footerIcon}>
                        <a href="https://oversea.gcgenome.com/" target="_blank" rel="noopener noreferrer">
                            <Image
                                className={style.avatarIcon}
                                src={getIconSrc("icon images-04")}
                                width={50}
                                height={50}
                                onMouseEnter={() => handleMouseEnter("icon images-04")}
                                onMouseLeave={handleMouseLeave}
                                alt="Social Icon 1"
                            />
                        </a>
                        <a href="https://www.instagram.com/gcgenome/" target="_blank" rel="noopener noreferrer">
                            <Image
                                className={style.avatarIcon}
                                src={getIconSrc("icon images-03")}
                                width={50}
                                height={50}
                                onMouseEnter={() => handleMouseEnter("icon images-03")}
                                onMouseLeave={handleMouseLeave}
                                alt="Social Icon 2"
                            />
                        </a>
                        <a href="https://www.youtube.com/@gcgenome2121" target="_blank" rel="noopener noreferrer">
                            <Image
                                className={style.avatarIcon}
                                src={getIconSrc("icon images-02")}
                                width={50}
                                height={50}
                                onMouseEnter={() => handleMouseEnter("icon images-02")}
                                onMouseLeave={handleMouseLeave}
                                alt="Social Icon 3"
                            />
                        </a>
                        <a href="https://www.linkedin.com/company/73449146/admin/feed/posts/" target="_blank"
                           rel="noopener noreferrer">
                            <Image
                                className={style.avatarIcon}
                                src={getIconSrc("icon images-01")}
                                width={50}
                                height={50}
                                onMouseEnter={() => handleMouseEnter("icon images-01")}
                                onMouseLeave={handleMouseLeave}
                                alt="Social Icon 4"
                            />
                        </a>
                    </div>
                </div>
                <div className={style.flex}>
                    <div className={style.footerCertificate}>
                        <Image src={footerImg} alt="img"/>
                    </div>
                </div>
            </div>
        </footer>
    )
}
