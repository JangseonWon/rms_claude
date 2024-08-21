import style from "@/app/(afterLogin)/request/services/[service]/single/page.module.css";
import MainPage from "@/app/(afterLogin)/request/service-catalog/_component/MainPage";
import CategoryTitle from "@/app/(afterLogin)/request/service-catalog/_component/CategoryTitle";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.orderContainerWrapper}>
                <div className={style.titleContainer}>
                    <CategoryTitle/>
                </div>
                <div className={style.orderContainer}>
                    <MainPage/>
                </div>
            </div>
        </div>
    )
}