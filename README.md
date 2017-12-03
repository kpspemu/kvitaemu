CPU	Quad-core ARM Cortex-A9 MPCore
Memory	512 MB RAM, 128 MB VRAM

http://infocenter.arm.com/help/index.jsp?topic=/com.arm.doc.ddi0407i/index.html

```
def : Processor<"cortex-a9-mp",     CortexA9Itineraries,
                                    [ProcA9, HasV7Ops, FeatureNEON, FeatureDB,
                                     FeatureDSPThumb2, FeatureMP]>;
```