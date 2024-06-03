import style from "@/app/_component/footer.module.css";
import Avatar from '@mui/material/Avatar';
import Stack from '@mui/material/Stack';
import Link from "next/link";
import React from "react";
import Image from "next/image";
import footerImg from "@/../public/footer-img.png"

export default function Footer() {
    return (
        <footer className={style.footer}>
            <div className={style.menu}>
                <div className={style.flex}>
                    <div className={style.div1Menu}>
                        <label className={style.followUsOn}>Follow Us On</label>
                    </div>
                    <div className={style.div2Menu}>
                        <Link href={"https://www.linkedin.com/company/73449146/admin/feed/posts"} className={style.footerMenu}>Visit our webpage</Link>
                        <Link href={"/home"} className={style.footerMenu}>Contact us</Link>
                        <Link href={"/home"} className={style.footerMenu}>imprint</Link>
                        <Link href={"/home"} className={style.footerMenu}>CentoCard® Instructions</Link>
                        <Link href={"/home"} className={style.footerMenu}>Data protection</Link>
                        <Link href={"/home"} className={style.footerMenu}>Terms of use</Link>
                        <Link href={"/home"} className={style.footerMenu}>Terms and conditions</Link>
                    </div>
                </div>
                <div className={style.flex}>
                    <div>
                        <Stack className={style.div3Menu} direction="row" spacing={2}>
                            <Link href={"https://oversea.gcgenome.com/"}>
                                <Avatar alt="Test1" src="/gc-logo_avatar.png" />
                            </Link>
                            <Link href={"https://www.instagram.com/gcgenome/"}>
                                <Avatar alt="Test2" src="/insta_avatar.png" />
                            </Link>
                            <Link href={"https://www.youtube.com/@gcgenome2121"}>
                                <Avatar alt="Test3" src="/youtube_avatar.png" />
                            </Link>
                            <Link href={"https://www.linkedin.com/company/73449146/admin/feed/posts/"}>
                                <Avatar alt="Test4" src="/linkdin_avatar.png" />
                            </Link>
                        </Stack>
                    </div>
                    <div className={style.div4Menu}>
                        <Image src={footerImg} alt="img"/>
                    </div>
                </div>
            </div>
        </footer>
    )
}
