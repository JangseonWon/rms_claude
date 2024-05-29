import Title from "@/app/_component/Title";
import style from "@/app/(afterLogin)/request/result/resample/page.module.css";
import ReSampleTable from "@/app/(afterLogin)/request/result/resample/_component/ReSampleTable";
import DatePick from "@/app/(afterLogin)/request/result/_component/DatePick";
import SearchInputBox from "@/app/(afterLogin)/request/result/_component/SearchInputBox";

export default async function Page() {
    return(
        <div className={style.container}>
            <section className={style.titleContainer}>
                <Title/>
            </section>
            <section className={style.mainContainer}>
                <div>
                    <div className={style.calendarAndSearch}>
                        <div className={style.dateFromTo}>
                            <DatePick/>
                        </div>
                        <div className={style.searchInput}>
                            <SearchInputBox/>
                        </div>
                    </div>
                    <button className={style.reSampleButton}>
                        Re-sample
                    </button>
                    <div>
                        <ReSampleTable/>
                    </div>
                </div>
            </section>
        </div>
    )
}