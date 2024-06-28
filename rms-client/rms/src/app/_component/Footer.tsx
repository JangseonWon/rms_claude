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
                </div>
                <div className={style.flex}>
                    <div>
                        <Stack className={style.div3Menu} direction="row" spacing={2}>
                            <Link href={"https://oversea.gcgenome.com/"} target={"_blank"}>
                                <Avatar alt="gc" src="/gc-logo_avatar.png" style={{ width: '4.5vh', height: '4.5vh'}}/>
                            </Link>
                            <Link href={"https://www.instagram.com/gcgenome/"} target={"_blank"}>
                                <Avatar alt="insta" src="/insta_avatar.png" style={{ width: '4.5vh', height: '4.5vh'}}/>
                            </Link>
                            <Link href={"https://www.youtube.com/@gcgenome2121"} target={"_blank"}>
                                <Avatar alt="youtube" src="/youtube_avatar.png" style={{ width: '4.5vh', height: '4.5vh'}}/>
                            </Link>
                            <Link href={"https://www.linkedin.com/company/73449146/admin/feed/posts/"} target={"_blank"}>
                                <Avatar alt="linkedin" src="/linkdin_avatar.png" style={{ width: '4.5vh', height: '4.5vh'}}/>
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
