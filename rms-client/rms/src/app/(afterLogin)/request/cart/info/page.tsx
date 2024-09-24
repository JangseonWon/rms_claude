import Info from "@/app/(afterLogin)/request/cart/_component/Info";
import {Suspense} from "react";

export default async function Page() {
    return(
        <Suspense>
            <div>
                <Info/>
            </div>
        </Suspense>
    )
}