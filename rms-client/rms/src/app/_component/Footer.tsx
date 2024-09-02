'use client';

import style from "@/app/_component/footer.module.css";
import Link from "next/link";
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
                <div className={style.flex}>
                    <div className={style.div1Menu}>
                        <label className={style.followUsOn}>Follow Us On</label>
                    </div>
                </div>
                <div className={style.flex}>
                    <div>
                        {/*<Stack className={style.div3Menu} direction="row" spacing={2}>
                            <Link href={"https://oversea.gcgenome.com/"} target={"_blank"}>
                                <Avatar
                                    alt="gc"
                                    src={getIconSrc("icon images-04")}
                                    className={style.avatarIcon}
                                    onMouseEnter={() => handleMouseEnter("icon images-04")}
                                    onMouseLeave={handleMouseLeave}
                                />
                            </Link>
                            <Link href={"https://www.instagram.com/gcgenome/"} target={"_blank"}>
                                <Avatar
                                    alt="insta"
                                    src={getIconSrc("icon images-03")}
                                    className={style.avatarIcon}
                                    onMouseEnter={() => handleMouseEnter("icon images-03")}
                                    onMouseLeave={handleMouseLeave}
                                />
                            </Link>
                            <Link href={"https://www.youtube.com/@gcgenome2121"} target={"_blank"}>
                                <Avatar
                                    alt="youtube"
                                    src={getIconSrc("icon images-02")}
                                    className={style.avatarIcon}
                                    onMouseEnter={() => handleMouseEnter("icon images-02")}
                                    onMouseLeave={handleMouseLeave}
                                />
                            </Link>
                            <Link href={"https://www.linkedin.com/company/73449146/admin/feed/posts/"} target={"_blank"}>
                                <Avatar
                                    alt="linkedin"
                                    src={getIconSrc("icon images-01")}
                                    className={style.avatarIcon}
                                    onMouseEnter={() => handleMouseEnter("icon images-01")}
                                    onMouseLeave={handleMouseLeave}
                                />
                            </Link>
                        </Stack>*/}
                    </div>
                    <div className={style.div4Menu}>
                        <Image src={footerImg} alt="img"/>
                    </div>
                </div>
            </div>
        </footer>
    )
}
