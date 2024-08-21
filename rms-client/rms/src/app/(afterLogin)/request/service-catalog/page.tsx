import style from "@/app/(afterLogin)/request/services/[service]/single/page.module.css";
import MainPage from "@/app/(afterLogin)/request/service-catalog/_component/MainPage";
import Title from "@/app/_component/Title";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.orderContainerWrapper}>
                <div className={style.titleContainer}>
                    <Title/>
                </div>
                <div className={style.orderContainer}>
                    <MainPage/>
                </div>
            </div>
        </div>
    )
}