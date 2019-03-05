/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.analysis.ko.dict;

import java.io.IOException;

import org.apache.lucene.util.fst.FST.Arc;
import org.apache.lucene.util.fst.FST;

/**
 * Thin wrapper around an FST with root-arc caching for Hangul syllables (11,172 arcs).
 */
public final class TokenInfoFST {
  private final FST<Long> fst;

  private final int cacheCeiling;
  private final Arc<Long> rootCache[];
  
  public final Long NO_OUTPUT;

  public TokenInfoFST(FST<Long> fst) throws IOException {
    this.fst = fst;
    this.cacheCeiling = 0xD7A3;
    NO_OUTPUT = fst.outputs.getNoOutput();
    rootCache = cacheRootArcs();
  }
  
  @SuppressWarnings({"rawtypes","unchecked"})
  private Arc<Long>[] cacheRootArcs() throws IOException {
    Arc<Long> rootCache[] = new Arc[1+(cacheCeiling-0xAC00)];
    Arc<Long> firstArc = new Arc<>();
    fst.getFirstArc(firstArc);
    Arc<Long> arc = new Arc<>();
    final FST.BytesReader fstReader = fst.getBytesReader();
    // TODO: jump to AC00, readNextRealArc to ceiling? (just be careful we don't add bugs)
    for (int i = 0; i < rootCache.length; i++) {
      if (fst.findTargetArc(0xAC00 + i, firstArc, arc, fstReader) != null) {
        rootCache[i] = new Arc<Long>().copyFrom(arc);
      }
    }
    return rootCache;
  }

  public Arc<Long> findTargetArc(int ch, Arc<Long> follow, Arc<Long> arc, boolean useCache, FST.BytesReader fstReader) throws IOException {
    if (useCache && ch >= 0xAC00 && ch <= cacheCeiling) {
      assert ch != FST.END_LABEL;
      final Arc<Long> result = rootCache[ch - 0xAC00];
      if (result == null) {
        return null;
      } else {
        arc.copyFrom(result);
        return arc;
      }
    } else {
      return fst.findTargetArc(ch, follow, arc, fstReader);
    }
  }
  
  public Arc<Long> getFirstArc(Arc<Long> arc) {
    return fst.getFirstArc(arc);
  }

  public FST.BytesReader getBytesReader() {
    return fst.getBytesReader();
  }

  /** @lucene.internal for testing only */
  FST<Long> getInternalFST() {
    return fst;
  }
}
