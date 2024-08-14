import style from './page.module.css';
import ExtensionTable from "@/app/(afterLogin)/request/management/extension/_component/ExtensionTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <ExtensionTable/>
        </div>
    )
}