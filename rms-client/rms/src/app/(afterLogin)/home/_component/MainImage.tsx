import style from "@/app/(afterLogin)/home/_component/MainImage.module.css";

export default function MainImage() {
    return (
        <div className={style.imageContainer}>
            <img src={"/home_main.jpg"} alt={"genome"}/>
            <div className={style.text}>
                <p className={style.mainText}>The Smart Solution</p>
                <p className={style.mainText}>For Rare Disease Testing</p>
                <p className={style.subText}>OUR DIAGNOSTIC SOLUTIONS ARE MORE THAN</p>
                <p className={style.subText}>LABORATORY AND BIOINFORMATICS</p>
            </div>
        </div>
    )
}