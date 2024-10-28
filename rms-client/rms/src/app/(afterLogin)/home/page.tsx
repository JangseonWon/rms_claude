import MainImage from "@/app/(afterLogin)/home/_component/MainImage";
import Statistics from "@/app/(afterLogin)/home/_component/Statistics";
import TestOption from "@/app/(afterLogin)/home/_component/TestOption";
import NavMenu from "@/app/(afterLogin)/_component/NavMenu";
import style from './page.module.css';

export default async function Page() {
    return(
        <div className={style.container}>
            <MainImage/>
            <section className={style.bodyContainer}>
                <div className={style.leftSection}>
                    <NavMenu/>
                </div>
                <div className={style.rightSection}>
                    <Statistics/>
                    <TestOption/>
                </div>
            </section>
        </div>
    )
}