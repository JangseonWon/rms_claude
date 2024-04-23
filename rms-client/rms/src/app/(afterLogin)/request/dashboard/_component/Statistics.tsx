import style from "@/app/(afterLogin)/request/dashboard/_component/statistics.module.css"

export default function Statistics() {
    return (
        <>
            <div className={style.container}>
                <div className={style.card}>
                    <div className={style.cardLabel}>Total</div>
                    <div className={style.cardValue}>83</div>
                </div>
                <div className={style.card}>Ordered</div>
                <div className={style.card}>In progress</div>
                <div className={style.card}>Test failed</div>
                <div className={style.card}>Delivered</div>
                <div className={style.card}>Complete</div>
            </div>
        </>
    )
}