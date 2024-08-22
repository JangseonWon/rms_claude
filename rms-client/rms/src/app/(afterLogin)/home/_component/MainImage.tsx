import Image from "next/image";
import homeMainImg from "@/../public/home_main.jpg"
import style from "@/app/(afterLogin)/home/_component/MainImage.module.css";

export default function MainImage() {
    return (
        <div className={style.imageContainer}>
            <Image src={homeMainImg} alt={"genome"}/>
            <div className={style.text}>
                <p className={style.mainText}>Care to Cure,</p>
                <p className={style.mainText}>We make it happen</p>
                <p className={style.subText}>We are dedicated to connect the care and cure to the world by providing genetic diagnosis</p>
                <p className={style.subText}>and suggesting personalized treatment for longer and healthier lives.</p>
            </div>
        </div>
    )
}